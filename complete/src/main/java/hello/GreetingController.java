package hello;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

	@GetMapping("/logs/{fid}")
	public String fileLog(@PathVariable final String fid, @RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		String content = readFile(fid);
		model.addAttribute("fid", content);
		return "logs";
	}

	private String readFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		Resource resource = new ClassPathResource(fileName);
		try {
			InputStream input = resource.getInputStream();
			File file = resource.getFile();
			try (Stream<String> stream = Files.lines(file.toPath())){//Paths.get(fileName))) {
				stream.forEach(sb::append);
				sb.append("\n");
				} 
			catch (IOException e) {
					e.printStackTrace();
				}
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		return sb.toString();
	}

}
