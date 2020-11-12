package com.surpriseme.user.activity.chooselanguage

data class ChooseLanguageModel (

    var data:ArrayList<LanguageModel>
)
data class LanguageModel(
    var lang_name:String = "",
    var lang_code:String = "",
)