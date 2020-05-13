package nachoymica.com.ar;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.google.gson.Gson;

import controlador.Controlador;
import exceptions.EdificioException;

import exceptions.PersonaException;
import exceptions.ReclamoException;
import exceptions.UnidadException;
import nachoymica.com.ftp.FTPConnect;
import nachoymica.com.ftp.ToHash;
import views.EdificioView;
import views.ImagenView;
import views.PersonaView;
import views.ReclamoView;
import views.UnidadView;

/**
 * Handles requests for the application home page.
 */

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private boolean loggedSuccess;
	private String usr;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) throws IOException {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		//test de carga de imagenes con archivo mock
		/*File file = new File("C:\\Users\\ignac\\Desktop\\INTERACTIVAS\\mancha.png");
		FTPConnect.uploadFile(file);
		*/
		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}

	//para poner parametros
	//localhost:8080/Metodo?param1=1&param2=2
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------
	
	@RequestMapping(value="/getEdificios", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String getEdificios() {
		Gson gson =  new Gson();
		Controlador ctrl = Controlador.getInstancia();
		List<EdificioView> edificios = ctrl.getEdificios();
		return gson.toJson(edificios);
	}	
	
	@RequestMapping(value = "/getUnidadesPorEdificio", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String getUnidadesPorEdificio(@RequestParam(value="codigo", required=true) int codigo) throws JsonProcessingException {
		try {
			List<UnidadView> unidad = Controlador.getInstancia().getUnidadesPorEdificio(codigo);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(unidad);
		}
		catch (EdificioException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}
	

	@RequestMapping(value = "/habilitadosPorEdificio", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String habilitadosPorEdificio(@RequestParam(value="codigo", required=true) int codigo) throws JsonProcessingException {
		try {
			List<PersonaView> personas = Controlador.getInstancia().habilitadosPorEdificio(codigo);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(personas);
		}
		catch (EdificioException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}
	
	@RequestMapping(value = "/dueniosPorEdificio", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String dueniosPorEdificio(@RequestParam(value="codigo", required=true) int codigo) throws JsonProcessingException {
		try {
			List<PersonaView> duenios = Controlador.getInstancia().dueniosPorEdificio(codigo);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(duenios);
		}
		catch (EdificioException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = "/habitantesPorEdificio", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String habitantesPorEdificio(@RequestParam(value="codigo", required=true) int codigo) throws JsonProcessingException {
		try {
			List<PersonaView> habitados = Controlador.getInstancia().habitantesPorEdificio(codigo);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(habitados);
		}
		catch (EdificioException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}

	
	@RequestMapping(value = "/dueniosPorUnidad", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String dueniosPorUnidad(@RequestParam(value="codigo",required=true) int codigo,
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero) throws JsonProcessingException {
		try {
			List<PersonaView> duenios = Controlador.getInstancia().dueniosPorUnidad(codigo,piso,numero);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(duenios);
		}
		catch (UnidadException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}
	

	@RequestMapping(value = "/inquilinosPorUnidad", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String inquilinosPorUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero) throws JsonProcessingException {
		try {
			List<PersonaView> inquilinos = Controlador.getInstancia().inquilinosPorUnidad(codigo,piso,numero);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(inquilinos);
		}
		catch (UnidadException e) { 
			logger.info(e.getMessage());
			return null;
		}
	}
	

	
	
	
	@RequestMapping(value = "/transferirUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void transferirUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero,
			@RequestParam(value = "documento", required = true) String documento) throws JsonProcessingException {
		try {
			Controlador.getInstancia().transferirUnidad(codigo, piso, numero, documento);
			
		}
		catch (UnidadException | PersonaException e) { 
			logger.info(e.getMessage());
			
		}
	}

	@RequestMapping(value = "/agregarDuenioUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void agregarDuenioUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero,
			@RequestParam(value = "documento", required = true) String documento) throws JsonProcessingException {
		try {
			Controlador.getInstancia().agregarDuenioUnidad(codigo, piso, numero, documento);
			
		}
		catch (UnidadException | PersonaException e) { 
			logger.info(e.getMessage());
		}
	}
	
	@RequestMapping(value = "/alquilarUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void alquilarUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero,
			@RequestParam(value = "documento", required = true) String documento) throws JsonProcessingException {
		try {
			Controlador.getInstancia().alquilarUnidad(codigo, piso, numero, documento);
			
		}
		catch (UnidadException | PersonaException e) { 
			logger.info(e.getMessage());
		}
	}

	@RequestMapping(value = "/agregarInquilinoUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void agregarInquilinoUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero,
			@RequestParam(value = "documento", required = true) String documento) throws JsonProcessingException {
		System.out.println(codigo);
		try {
			Controlador.getInstancia().agregarInquilinoUnidad(codigo, piso, numero, documento);
			
		}
		catch (UnidadException | PersonaException e) { 
			logger.info(e.getMessage());
		}
	}


	
	@RequestMapping(value = "/liberarUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void liberarUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero) throws JsonProcessingException {
		try {
			Controlador.getInstancia().liberarUnidad(codigo, piso, numero);
			
		}
		catch (UnidadException e) { 
			logger.info(e.getMessage());
		}
		
	}
	
	@RequestMapping(value = "/habitarUnidad", method = RequestMethod.POST)
	public @ResponseBody<json> void habitarUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero) throws JsonProcessingException {
		try {
			Controlador.getInstancia().habitarUnidad(codigo, piso, numero);
			
		}
		catch (UnidadException e) { 
			logger.info(e.getMessage());
		}
		
	}
	
	@RequestMapping(value="/imagenesPorReclamo", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String imagenesPorReclamo(@RequestParam("nroreclamo") int nroreclamo) {
		Controlador ctrl = Controlador.getInstancia();
		Gson json = new Gson();
		try {
				List<ImagenView> imgv = ctrl.getImagenes(nroreclamo);
				return json.toJson(imgv);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	

	@RequestMapping(value="/agregarPersona", method = RequestMethod.GET)
	public @ResponseBody<json> void agregarPersona(@RequestParam("documento") String documento, @RequestParam("nombre") String nombre) {
		Controlador ctrl = Controlador.getInstancia();
		try {
			
			ctrl.agregarPersona(documento, nombre);
		} catch (Exception e) {
			//logger.info(e.printStackTrace());
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/eliminarPersona", method = RequestMethod.POST)
	public @ResponseBody<json> void eliminarPersona( @RequestParam(value="documento",required=true)String documento) throws JsonProcessingException {
		try {
			Controlador.getInstancia().eliminarPersona(documento);;
			
		}
		catch (PersonaException e) { 
			logger.info(e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/reclamosPorEdificio", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String reclamosPorEdificio(@RequestParam(value="codigo",required=true) int codigo) throws JsonProcessingException {
			List<ReclamoView> reclamos = Controlador.getInstancia().reclamosPorEdificio(codigo);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(reclamos);
		
	}
	
	@RequestMapping(value = "/reclamosPorUnidad", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String reclamosPorUnidad(@RequestParam(value="codigo",required=true) int codigo, 
			@RequestParam(value="piso",required=true)String piso,
			@RequestParam(value="numero",required=true) String numero) throws JsonProcessingException {
		
			List<ReclamoView> reclamos = Controlador.getInstancia().reclamosPorUnidad(codigo,piso,numero);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(reclamos);
		
	}
	
	@RequestMapping(value = "/reclamosPorNumero", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String reclamosPorUnidad(@RequestParam(value="numero",required=true) int numero) throws JsonProcessingException {
		ReclamoView reclamo = Controlador.getInstancia().reclamosPorNumero(numero);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(reclamo);
	}
	
	@RequestMapping(value = "/reclamosPorPersona", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String reclamosPorPersona(@RequestParam(value="documento",required=true) String documento) throws JsonProcessingException {
		List<ReclamoView> reclamos = Controlador.getInstancia().reclamosPorPersona(documento);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(reclamos);
	}
 
	/*@RequestMapping(value = "/agregarReclamo", method = RequestMethod.GET)
	public @ResponseBody<json> void agregarReclamo(@RequestParam(value="codigp",required=true)int codigo,
			@RequestParam(value = "piso", required = true) String piso,
			@RequestParam(value = "numero", required = true) String numero,
			@RequestParam(value = "documento", required = true) String documento,
			@RequestParam(value = "ubicacion", required = true) String ubicacion,
			@RequestParam(value = "descripcion", required = true) String descripcion,
			@RequestParam(value = "estado", required = true) String estado) throws JsonProcessingException {
		try {
			Controlador.getInstancia().agregarReclamo(codigo, piso, numero, documento, ubicacion, descripcion, estado);
			
		}
		catch (EdificioException | UnidadException | PersonaException e) { 
			logger.info(e.getMessage());
		}
	}*/
	
	@RequestMapping(value="/agregarReclamo", method = RequestMethod.POST)
	public @ResponseBody<json> void agregarReclamo(@RequestParam("codigo") int codigoEdificio,
													@RequestParam("piso") String piso, 
													@RequestParam("numero") String numero, 
													@RequestParam("documento") String documento,
													@RequestParam("ubicacion") String ubicacion,
													@RequestParam("descripcion") String descripcion) {
		Controlador ctrl = Controlador.getInstancia();
		
		try {
				ctrl.agregarReclamo(codigoEdificio, piso, numero, documento, ubicacion, descripcion);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	/*
	@RequestMapping(value = "/agregarImagenAReclamo", method = RequestMethod.GET)
	public @ResponseBody<json> void agregarImagenAReclamo(@RequestParam(value="numero",required=true)int numero,
			@RequestParam(value = "direccion", required = true) String direccion,
			@RequestParam(value = "tipo", required = true) String tipo) throws JsonProcessingException {
		try {
			Controlador.getInstancia().agregarImagenAReclamo(numero,direccion,tipo);;
			
		}
		catch (ReclamoException e) { 
			logger.info(e.getMessage());
		}
	}*/
	

	@RequestMapping(value="/agregarImagenReclamo",method = { RequestMethod.POST, RequestMethod.PUT }) //No importa el error que tira por consola, anda flamoide
	 public void agregarImagenReclamo(@RequestParam("nroreclamo") int nroreclamo, @RequestParam("id") String id) {
		 try {
			Controlador.getInstancia().agregarImagenAReclamo(nroreclamo, id, "imagen");
		} catch (ReclamoException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/cambiarEstado", method = RequestMethod.POST)
	public @ResponseBody<json> void cambiarEstado(@RequestParam(value="numero",required=true)int numero,
			@RequestParam(value = "estado", required = true) String estado) throws JsonProcessingException {
		try {
			Controlador.getInstancia().cambiarEstado(numero, estado);
			
		}
		catch (ReclamoException e) { 
			logger.info(e.getMessage());
		}
	}
	

	
	/*@RequestMapping(value="/login", method= RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> boolean login(@RequestParam("usr") String usr, @RequestParam("pwd") String pwd) throws JsonProcessingException {
		Controlador ctrl =  Controlador.getInstancia();
		Gson json = new Gson();
		System.out.println(usr);
		System.out.println(pwd);
		if (usr.equals("admin")&&pwd.equals("admin")) {
			System.out.println(3215534);
			this.loggedSuccess = true;
			this.usr = pwd;
			return this.loggedSuccess;
		} else {
			this.loggedSuccess = false;
			try {
				this.loggedSuccess = ctrl.login(usr, pwd);
				if (this.loggedSuccess == true)  {
					this.usr = pwd;
				
					System.out.println("Usuario logeado con exito");
					System.out.println(this.loggedSuccess);
					return this.loggedSuccess;
				} else {
					System.out.println("Error de autenticacion");
				}
			} catch (Exception e) {
				System.out.println("El usuario no existe");
			}
		}
		return this.loggedSuccess;
	}*/
	
	@RequestMapping(value="/login", method= RequestMethod.POST)
	public @ResponseBody<json> void login(@RequestParam("usr") String usr, @RequestParam("pwd") String pwd) {
		Controlador ctrl =  Controlador.getInstancia();
		Gson json = new Gson();
		if (usr.equals("admin")&&pwd.equals("admin")) {
			this.loggedSuccess = true;
			this.usr = pwd;
		} else {
			this.loggedSuccess = false;
			try {
				this.loggedSuccess = ctrl.login(usr, pwd);
				if (this.loggedSuccess == true)  {
					this.usr = pwd;
					System.out.println("Usuario logeado con exito");
				} else {
					System.out.println("Error de autenticacion");
				}
			} catch (Exception e) {
				System.out.println("El usuario no existe");
			}
		}
	}
	
	
	@RequestMapping(value="/loggedSucces", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String isLogged() {
		Gson json = new Gson();
		return json.toJson(this.loggedSuccess);
	}
	
	
	@RequestMapping(value="/logOff", method= RequestMethod.POST)
	public @ResponseBody<json> void logOff() {
		this.loggedSuccess = false;
		this.usr = null;
	} 
	
	@RequestMapping(value="/getUsrInfo", method = RequestMethod.GET, produces = {"application/json"})
	public @ResponseBody<json> String getUsrInfo() throws PersonaException {
		Controlador ctrl = Controlador.getInstancia();
		Gson json = new Gson();
		if (this.loggedSuccess && this.usr != null &&!this.usr.isEmpty() && !this.usr.equals("admin"))
			return json.toJson(ctrl.userInfo(this.usr));
		else 
			return null;
	}
	
}
