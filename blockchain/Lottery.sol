// SPDX-License-Identifier: GPL-3.0
pragma solidity ^0.7.5;

contract Lottery{
    uint256 public prizePool;
    uint256 public price;
    
    address croupier;
    address payable[] players;
    
    constructor() {
        price = 0.01 ether;
        croupier = msg.sender;
    }
    
    modifier onlyCroupier() {
        require(msg.sender == croupier, "Only croupier can call this.");
        _;
    }
    
    function buyLottery() public payable{
        require(msg.value == price, "Incorrect price");
        players.push(msg.sender);
        prizePool =  address(this).balance;
    }
    
    function drawLottery() public onlyCroupier{
        require(players.length > 0,"No player participated");
        players[random(players.length)].transfer(address(this).balance);
        prizePool = address(this).balance;
        players = new address payable[](0);
    }
    
    function random(uint range) private view returns (uint) {
        return uint(keccak256(abi.encodePacked(block.timestamp, block.difficulty))) % range;
    }
}
