package com.skailab.nikabuy.models

class Deposit {
    var id:Int?=0
    var transactionTypeId:Int?=0
    var TransactionType:TransactionType?=null
    var amount:Double?=null
    var referenceNumber:String?=""
    var buyerId:Int?=0
    var buyer:Buyer?=null
    var remark:String?=""
    var status:String?=""
    var dateText:String?=""
    var depositAccount:DepositAccount?=null
    var imageUrl:String?=""
}