import $exec.effect
import $exec.^.util.color
import $exec.^.util.util
import com.github.nscala_time.time.Imports._

class Off extends Effect {
  override def render(index: Int, strip: Strip, time: DateTime): Color = {
    Color(0, 0, 0)
  }
}
