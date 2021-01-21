#include <stdio.h>
#include "sdkconfig.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_spi_flash.h"
#include "freertos/queue.h"
#include "esp_err.h"
#include "esp_sntp.h"
#include "esp_timer.h"
#include "driver/touch_pad.h"
#include "driver/adc.h"
#include "driver/gpio.h"
#include "esp_intr_alloc.h"
#include "esp_adc_cal.h"
#include <math.h>

// Práctica 4 - Cronómetro

#define GPIO_TIMER_0 22
#define GPIO_TIMER_1 23
#define GPIO_TIMER_SEL_0 1ULL<<GPIO_TIMER_0
#define GPIO_TIMER_SEL_1 1ULL<<GPIO_TIMER_1
#define DEFAULT_VREF 1100

#ifdef CONFIG_SENSOR_INFRA
    static const adc_unit_t unit = ADC_UNIT_1;
    static const adc_atten_t atten = ADC_ATTEN_DB_11;
    static const adc_bits_width_t width = ADC_WIDTH_BIT_12;
    static esp_adc_cal_characteristics_t *adc_chars; 
    static const adc_channel_t channel = ADC_CHANNEL_6;

    static void infra_sensor_init(void){
        adc1_config_width(width);
        adc1_config_channel_atten(channel, atten);
        adc_chars = calloc(1, sizeof(esp_adc_cal_characteristics_t));
        esp_adc_cal_value_t val_type = esp_adc_cal_characterize(unit, atten, width, DEFAULT_VREF, adc_chars);
    }   
#endif


xTaskHandle StartStopHandle;
xTaskHandle ResetHandle;
xTaskHandle RefreshHandle;
bool activo = false;
bool valor_pin = false;
int32_t min = 0;
int32_t sec = 0;
static uint32_t s_pad_init_val[TOUCH_PAD_MAX];


void callback_timer(){
    if (!valor_pin){
        gpio_set_level(GPIO_TIMER_0, 1);
        valor_pin = true;
    } else {
        gpio_set_level(GPIO_TIMER_0, 0);
        valor_pin = false;
    }

}

static void tp_isr_handler(){
    vTaskResume(StartStopHandle);
}

static void IRAM_ATTR timer_isr(void * arg){
    vTaskResume(RefreshHandle);
}

void StartStopTask (){
    while (1){
        vTaskSuspend(StartStopHandle);
        if (activo){
            printf("\nStop\n");
            activo = false;
        }else {
            printf("\nStart\n");
            activo = true;
        }
        vTaskDelay(200);
    }
}

#ifdef CONFIG_SENSOR_HALL
void ResetTask (){
    while (1){
        int32_t num = hall_sensor_read();
        if (abs(num) >= 100){
            sec = 0;
            min = 0;
            activo = false;

        }
        vTaskDelay(1500 / portTICK_PERIOD_MS);
    }
}

#endif


#ifdef CONFIG_SENSOR_INFRA

void ResetTask (){
    while (1){
        uint32_t num = 0;
        uint32_t sample = 10;
        for (int i = 0; i < sample; i++){
            num = num + adc1_get_raw(channel);
        }
        num = num / sample;
        // Haciendo una regresión de potencia la relación voltaje/distancia queda:
        //  y=12.096442*x^−1 siendo y la distancia y x el voltaje
        uint32_t  volt =  (esp_adc_cal_raw_to_voltage(num, adc_chars));
        float dist = 12.096442f / ((float)volt / 1000);
        if (dist >= 4 && dist < 10 && (sec != 0 || min != 0 || activo == true )){
            sec = 0;
            min = 0;
            activo = false;
        }
        vTaskDelay(1500 / portTICK_PERIOD_MS);
    }
}

#endif



void RefreshTask (){
    while (1){
        vTaskSuspend(RefreshHandle);
        if (activo){
            sec += 1;
            if (sec == 60){
                min += 1;
                sec = 0;
            }
        }
        if (sec < 10 && min < 10){
            printf("\n%d%d:%d%d\n", 0, min, 0, sec);
        }
        else if (sec < 10 && min >= 10){
            printf("\n%d:%d%d\n", min, 0, sec);
        }
        else if (sec >= 10 && min < 10){
            printf("\n%d%d:%d\n", 0, min, sec);
        }
        else {
           printf("\n%d:%d\n", min, sec);

        }
    }
}

static void tp_set_thresholds(void)
{
    uint16_t touch_value;
    for (int i = 0; i < TOUCH_PAD_MAX; i++) {
        //read filtered value
        touch_pad_read_filtered(i, &touch_value);
        s_pad_init_val[i] = touch_value;
        printf("test init: touch pad [%d] val is %d", i, touch_value);
        //set interrupt threshold.
        ESP_ERROR_CHECK(touch_pad_set_thresh(i, touch_value * 1/3));

    }
}

void app_main()
{

    touch_pad_init();
    touch_pad_set_fsm_mode(TOUCH_FSM_MODE_TIMER);
    for (int i = 0;i< TOUCH_PAD_MAX;i++) {
        touch_pad_config(i, 0);
    }

    touch_pad_filter_start(1500);
    tp_set_thresholds();
    touch_pad_isr_register(tp_isr_handler, NULL);
    touch_pad_intr_enable();

    #ifdef CONFIG_SENSOR_INFRA
        infra_sensor_init();
    #endif

    #ifdef CONFIG_SENSOR_HALL
        adc_bits_width_t adc1_bits = 3;
        adc1_config_width(adc1_bits);
    #endif

    gpio_config_t timer_0_conf;
    gpio_config_t timer_1_conf;

    timer_0_conf.intr_type = GPIO_PIN_INTR_DISABLE;
    timer_0_conf.pin_bit_mask = GPIO_TIMER_SEL_0;
    timer_0_conf.mode = GPIO_MODE_OUTPUT;
    timer_0_conf.pull_up_en = 0;
    timer_0_conf.pull_down_en = 0;
    gpio_config(&timer_0_conf);

    timer_1_conf.intr_type = GPIO_PIN_INTR_ANYEDGE;
    timer_1_conf.pin_bit_mask = GPIO_TIMER_SEL_1;
    timer_1_conf.mode = GPIO_MODE_INPUT;
    timer_1_conf.pull_up_en = 1;
    gpio_config(&timer_1_conf);

    gpio_install_isr_service(0);
    gpio_isr_handler_add(GPIO_TIMER_1, timer_isr, NULL);

    const esp_timer_create_args_t timer_args = {
        .callback = &callback_timer,
        .name = "Timer Refresh"};
    esp_timer_handle_t timer_handle;
    esp_timer_create(&timer_args, &timer_handle);
    esp_timer_start_periodic(timer_handle, 1000000);

    

    

    xTaskCreate(&StartStopTask, "Tarea Start/Stop", 3072, NULL, 1, &StartStopHandle);
    xTaskCreate(&ResetTask, "Tarea Reset", 3072, NULL, 1, &ResetHandle);
    xTaskCreate(&RefreshTask, "Tarea Refresco", 3072, NULL, 3, &RefreshHandle);

}