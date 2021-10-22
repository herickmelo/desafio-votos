package com.example.demo.controller.rest;

import java.util.Optional;

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
	 * Cadastra uma nova pauta
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
	
	@GetMapping(path = "/test")
	public String getPauta() {
		//pautaService.salvar(pauta);
		return "Teste";
	}
}
