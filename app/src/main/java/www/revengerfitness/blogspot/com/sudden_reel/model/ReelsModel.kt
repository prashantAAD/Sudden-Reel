package www.revengerfitness.blogspot.com.sudden_reel.model

data class ReelsModel(var reelsId:String,
                      var reelsAt:Long,
                      var reelsBy:String,
                      var reelsVideo:String,
                      var reelsTitle:String,
                      var reelsStorageId:String
)

{
    constructor():this(
        "",
        0,
        "",
        "",
        "",
        ""
    )
}
