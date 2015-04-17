package debop4s.web.spring.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation._

@Controller
@RequestMapping(value = Array("/hello"))
class HelloWebController {

  @RequestMapping(method = Array(RequestMethod.GET))
  def printHello(model: ModelMap) = {
    model.addAttribute("message", "Hello Spring MVC Framework!")
    "hello"
  }
}
