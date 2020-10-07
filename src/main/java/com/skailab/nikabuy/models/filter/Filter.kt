package com.skailab.nikabuy.models.filter

import java.util.*

open class Filter {
    var page:Int=1
    var pageSize:Int=40
    var buyerId:Int=0
    var language:String?=""
    init {
        language=Locale.getDefault().language
        if(language.isNullOrEmpty()){
            language="en"
        }
        if(language=="zh"){
            language="zh-CN"
        }
    }
}