package www.revengerfitness.blogspot.com.sudden_reel.model

data class InformationModel(
    var userName:String,
    val userEmail:String,
    var userPassword:String,
    var userProfilePhoto:String,
    var userId:String,
    var userVerified:Boolean
    )
{

constructor():this(
    "",
    "",
    "",
    "",
    "",
    false
)

}
