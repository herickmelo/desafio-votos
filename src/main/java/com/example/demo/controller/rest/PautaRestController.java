package com.example.demo.controller.rest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.Associado;
import com.example.demo.entity.Pauta;
import com.example.demo.service.AssociadoService;
import com.example.demo.service.PautaService;
import com.example.demo.util.Reminder;

/**
 * Classe controladora que gerencia as requisições da aplicação
 * @author Herick
 *
 */
@RestController
@RequestMapping("/v1/pauta")
public class PautaRestController {
	
	@Autowired
	private PautaService pautaService;
	
	@Autowired
	private AssociadoService associadosService;

	/**
	 * Cadastra uma nova pauta
	 * @param pauta
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, headers = "Content-Type=application/json")
	public String cadastrarPauta(@RequestBody Pauta pauta) {
		pautaService.salvar(pauta);
		return "Pauta " + pauta.getId() + " cadastrada.";
	}
	
	/**
	 * Verifica se o eleitor está apto a votar
	 * @param pauta
	 * @return
	 */
	public Boolean verificarEleitor(Associado associado) {
        RestTemplate restTemplate = new RestTemplate();
        String fooResourceUrl = "https://user-info.herokuapp.com/users/" + associado.getCpf();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
			if (response.getBody().contains("UNABLE_TO_VOTE")) {
				return false;
			}else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Recebe os votos dos associados, remove os votos repetidos e verifica se a sessão de votação na pauta especificada está aberta
	 * @param pauta
	 * @return
	 */
	@RequestMapping(path = "/votos", method = RequestMethod.POST, headers = "Content-Type=application/json")
	public String receberVotos(@RequestBody List<Associado> listaDeAssociados) {
		String resultado = "";
		List<Associado> associadosSemRepeticao = listaDeAssociados.stream().filter(distinctByKey(Associado::getCpfAndPauta)).collect(Collectors.toList());
		for (Associado associado : associadosSemRepeticao) {
			if (verificarSessao(associado.getPauta().getId())) {
				if (verificarEleitor(associado)) {
					associadosService.salvar(associado);	
				}else {
					resultado += "Eleitor "+associado.getCpf()+ " não pode votar.\nVoto não computado.\n";
				}
			}else {
				resultado += "Sessão da pauta " +associado.getPauta().getId()+ " está fechada.\nVoto não computado.\n";
			}
		}
		if (resultado == "") {
			return "Todos os votos foram computados com sucesso.";
		}
		return resultado;
	}
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}
	
	/**
	 * Abre uma sessão de votação na pauta
	 * @param pauta
	 * @return
	 */
	@GetMapping(value = { "/abrirSessao/{idPauta}", "/abrirSessao/{idPauta}/{tempoAbertura}" })
	@ResponseBody
	public String abrirSessao(@PathVariable Long idPauta, @PathVariable(required = false) Integer tempoAbertura) {
		if(tempoAbertura == null) {
			tempoAbertura = 60;
		}
		Optional<Pauta> p =  pautaService.findById(idPauta);
		if (p.isPresent()) {
			p.get().setSessaoAberta(true);
			new Reminder(tempoAbertura, p.get().getId());
			pautaService.salvar(p.get());
		}else {
			return "Pauta não encontrada.";
		}
		return "Sessão aberta para votação.";
	}
	
	/**
	 * Fecha uma sessão de votação na pauta
	 * @param pauta
	 * @return
	 */
	@GetMapping(value = { "/fecharSessao/{idPauta}" })
	@ResponseBody
	public String fecharSessao(@PathVariable Long idPauta) {
		Optional<Pauta> p =  pautaService.findById(idPauta);
		if (p.isPresent()) {
			p.get().setSessaoAberta(false);
			pautaService.salvar(p.get());
		}else {
			return "Pauta não encontrada.";
		}
		return "Sessão fechada para votação.";
	}
	
	/**
	 * Verifica se uma sessão de votação está aberta ou fechada
	 * @param pauta
	 * @return
	 */
	public Boolean verificarSessao(@PathVariable Long idPauta) {
		Optional<Pauta> p =  pautaService.findById(idPauta);
		if (p.isPresent()) {
			return p.get().getSessaoAberta();
		}
		return false;
	}
	
	/**
	 * Contabiliza o resultado da votação
	 * @param idPauta
	 * @return
	 */
	@GetMapping(path = "/resultadoVotacao/{idPauta}")
	public String obterResultadoVotacao(@PathVariable Long idPauta) {
		List<Associado> listAssociados = associadosService.findByPautaId(idPauta);
		if (listAssociados == null || listAssociados.isEmpty()) {
			return "Sem votos para esta pauta";
		}
		int aFavor = 0;
		int contra = 0;
		for (Associado associado : listAssociados) {
			if (associado.isVoto()) {
				aFavor++;
			}else {
				contra++;
			}
		}
		return "Resultado da votação para a pauta "+idPauta+":\n Votos a favor: " + aFavor + " \n Votos contra: " + contra;
	}
}
