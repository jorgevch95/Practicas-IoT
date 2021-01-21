from bottle import route, run, template, request
from sklearn.neural_network import MLPClassifier

parameters = ['x1', 'x2']
y_name = 'y'
clf = MLPClassifier(solver='lbfgs', alpha=0.00001, hidden_layer_sizes=(5, 2), random_state=1)
x_training_data = []
y_training_data = []


@route('/train_data')
def train_data():
    x = []
    for parameter in parameters:
        x.append(int(request.query[parameter]))
    y = int(request.query[y_name])
    x_training_data.append(x)
    y_training_data.append(y)
    return 'x:{0}; y:{1}'.format(str(x), str(y))


@route('/fit')
def fit_data():
    clf.fit(x_training_data, y_training_data)
    return 'fit done'


@route('/predict')
def predict():
    query = []
    for parameter in parameters:
        query.append(int(request.query[parameter]))
    result = clf.predict([query])[0]
    return str(result)


run(host='localhost', port=8080)
