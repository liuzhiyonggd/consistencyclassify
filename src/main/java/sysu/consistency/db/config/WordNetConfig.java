package sysu.consistency.db.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.Lin;

@Configuration
public class WordNetConfig {

	@Bean(name="dictionary")
	public IRAMDictionary getDictionary(){
		//�����ʵ�
		String path = "C:/program files (x86)/WordNet/2.1/dict";
	    URL url = null;
		try {
			url = new URL("file", null, path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}  
		
		if(url==null){
			return null;
		}
		else{
	        IRAMDictionary dict=new RAMDictionary(url,ILoadPolicy.NO_LOAD);
	        try {
				dict.open();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return dict;
		}
	}
	
	@Bean(name="lin")
	public Lin getLin(){
		JWS    ws = new JWS("C:/program files (x86)/WordNet", "2.1");
        Lin lin = ws.getLin();
        return lin;
	}
	
	
}
