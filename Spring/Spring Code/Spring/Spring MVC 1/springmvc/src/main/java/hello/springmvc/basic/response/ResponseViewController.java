package hello.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResponseViewController {
    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1() {
        ModelAndView modelAndView = new ModelAndView("response/hello") // viewName
                .addObject("data", "hello!"); // Model
        return modelAndView;
    }

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model) {
        model.addAttribute("data", "hello!"); // model
        return "response/hello"; // viewName
    }

    @RequestMapping("/response/hello") // 컨트롤러 경로 = 뷰의 논리적 이름 (반환 값이 void라면, 동일하게 진행)
    public void responseViewV3(Model model) {
        model.addAttribute("data", "hello!");
    }
}
