package www.revengerfitness.blogspot.com.sudden_reel.model

data class FollowModel(
    var followedBy:String,
    var followedAt:Long,
)
{
    constructor():this(
        "",
        0
    )
}


