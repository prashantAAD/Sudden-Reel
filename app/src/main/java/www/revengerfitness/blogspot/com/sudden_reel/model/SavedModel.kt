package www.revengerfitness.blogspot.com.sudden_reel.model

data class SavedModel(var savedReel:String,var savedAt:Long)
{
    constructor():this(
    "",
        0
    )
}
