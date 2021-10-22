package com.example.demo.controller.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/pauta")
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
		/*for (Associado associado: pauta.getListaDeAssociados()) {
			associado.setPauta(pauta);
		}
		associadosService.salvarTodos(pauta.getListaDeAssociados());*/
		return "Pauta cadastrada";
	}
	
	/**
	 * Recebe os votos dos associados
	 * @param pauta
	 * @return
	 */
	@RequestMapping(path = "/votos", method = RequestMethod.POST, headers = "Content-Type=application/json")
	public String receberVotos(@RequestBody List<Associado> listaDeAssociados) {
		List<Associado> associadosSemRepeticao = listaDeAssociados.stream().filter(distinctByKey(Associado::getCpfAndPauta)).collect(Collectors.toList());
		associadosService.salvarTodos(associadosSemRepeticao);
		return "Votos contabilizados";
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
	@GetMapping(value = { "/abrirSessaoVotacao/{idPauta}", "/abrirSessaoVotacao/{idPauta}/{tempoAbertura}" })
	@ResponseBody
	public String abrirSessaoVotacao(@PathVariable Long idPauta, @PathVariable(required = false) Integer tempoAbertura) {
		if(tempoAbertura == null) {
			tempoAbertura = 60;
		}
		Optional<Pauta> p =  pautaService.findById(idPauta);
		if (p.isPresent()) {
			p.get().setSessaoAberta(true);
			new Reminder(tempoAbertura);
		}else {
			return "Pauta não encontrada.";
		}
		return "Sessão aberta para votação.";
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
