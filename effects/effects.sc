import $exec.effect

//User definded effects
import $exec.rainbow
import $exec.off

object Effects {
  def create(s: String): Effect = s match {
    case "rainbow" => new Rainbow()
    case default   => new Off()
  }
}
