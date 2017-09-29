import $exec.^.util.color
import $exec.^.util.util
import com.github.nscala_time.time.Imports._

trait Effect {
  def render(index: Int, strip: Strip, time: DateTime): Color
}
