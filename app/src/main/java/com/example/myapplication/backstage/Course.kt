package com.example.myapplication.backstage

data class DDlInfo(
    val Name:  String,                  //DDL名字
    val EndingTime : Long,              //DDL结束时间
    val Prompt : String,                //DDL描述
    val StartingTime : Long             //DDL开始工作时间
)

data class CourseTemplate(              //模板，对应以周为单位的日历上的一块\
    val info: CourseInfo,
    val Column: Short,                  //课程位于哪一列
    val StartingTime : Short,           //课程开始于哪一行
    val EndingTime : Short,             //课程结束于哪一行
    val Period: Long                    //一周一次或两周一次(应该不存在三天一次的课吧，
    // 一周两次的话就建立两个template)
)

data class CourseInfo(
    val Name:  String,                  //课程名字
    val StartingTime : Long,            //课程开始时间，直接使用时间戳
    val EndingTime : Long,              //课程结束时间，直接使用时间戳，(前端输入时可以选择持续多少周，但后端不记录)
    val TimeInfo : List<CourseTemplate>,//课程时间，存放CourseTemplate的List
    val Prompt : String,                //课程描述
    val Location: String,               //课程位置
)