package www.revengerfitness.blogspot.com.sudden_reel.model

data class NotificationModel(
    var notificationBy:String,
    var notificationAt:Long,
    var type: String,
    var reelsId:String,
    var notificationId:String,
    var reelsBy: String,
    var checkOpen:Boolean
)
{

    constructor():this(
        "",
        0,
        "",
        "",
        "",
        "",
        false
    )
}
