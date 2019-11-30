package hello;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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

	private boolean fileWatcherLogic(String fileName) throws IOException, InterruptedException {
		
		Resource resource = new ClassPathResource(fileName);
		File file = resource.getFile();
		final Path path = file.toPath(); //FileSystems.getDefault().getPath(System.getProperty("user.home"), "Desktop");
		
		System.out.println(path);
		try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
		    final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
		    while (true) {
		        final WatchKey wk = watchService.take();
		        for (WatchEvent<?> event : wk.pollEvents()) {
		            //we only register "ENTRY_MODIFY" so the context is always a Path.
		            final Path changed = (Path) event.context();
		            System.out.println(changed);
		            if (changed.endsWith(fileName)) {
		                System.out.println(String.format("%s  Changes", fileName));
		            }
		        }
		        // reset the key
		        boolean valid = wk.reset();
		        if (!valid) {
		            System.out.println("Key has been unregisterede");
		        }
		    }
		}	
	}
	
	private String readFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		Resource resource = new ClassPathResource(fileName);
		try {
			//InputStream input = resource.getInputStream();
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
