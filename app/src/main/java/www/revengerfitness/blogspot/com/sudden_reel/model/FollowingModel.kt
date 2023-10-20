package www.revengerfitness.blogspot.com.sudden_reel.model

data class FollowingModel(
    var followingTo:String,
    var followingAt:Long
)
{
    constructor():this(
        "",
        0
    )
}
