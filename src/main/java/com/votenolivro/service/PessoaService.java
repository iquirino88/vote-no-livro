package com.votenolivro.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.votenolivro.model.Livro;
import com.votenolivro.model.LivroVotado;
import com.votenolivro.model.Pessoa;
import com.votenolivro.repository.interfaces.IPessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private LivroService livroService;
	
	@Autowired
	private IPessoaRepository pessoaRepository;
	
	public Pessoa processarVotos(Pessoa pessoa, List<Livro> livros) throws Exception {
		validar(pessoa);
		Pessoa pessoaBanco = listarPessoaPorNomeEmail(pessoa.getNome(),pessoa.getEmail());
		if(pessoaBanco != null){
			pessoa = pessoaBanco;
		}
		livroService.processarVotos(livros);
		processarVotoPessoa(pessoa,livros);
		return pessoaRepository.saveOrUpdate(pessoa);
	}
	
	private void processarVotoPessoa(Pessoa pessoa, List<Livro> livros) {
		if(pessoa.getLivros() == null || pessoa.getLivros().isEmpty()){
			List<LivroVotado> listLivrosVotados = setLivrosVotados(livros);
			pessoa.setLivros(listLivrosVotados);
		}else{
			Map<Long, LivroVotado> map = mapTransformToMap(pessoa.getLivros());
			if(livros != null && !livros.isEmpty()){
				for (Livro livro : livros) {
					Long key = livro.getId();
					if(map.containsKey(key)){
						map.get(key).adicionarVoto();
					}else{
						map.put(key, new LivroVotado(livro));
					}
				}
			}
			pessoa.setLivros(new ArrayList<LivroVotado>(map.values()));
		}
	}


	private Map<Long, LivroVotado> mapTransformToMap(List<LivroVotado> livros) {
		Map<Long, LivroVotado> map = new HashMap<Long, LivroVotado>();
		if(livros != null && !livros.isEmpty()){
			for (LivroVotado livroVotado : livros) {
				Long key = livroVotado.getLivro().getId();
				map.put(key, livroVotado);
			}	
		}
		return map;
	}


	private List<LivroVotado> setLivrosVotados(List<Livro> livros) {
		List<LivroVotado> listLivrosVotados = new ArrayList<LivroVotado>();
		if(livros != null){
			for (Livro livro : livros) {
				listLivrosVotados.add(new LivroVotado(livro));
			}
		}
		return listLivrosVotados;
	}


	private void validar(Pessoa pessoa) throws Exception {
		if(pessoa == null){
			throw new Exception("");
		}
		if(pessoa.getNome() == null || pessoa.getNome().isEmpty()){
			throw new Exception("");
		}
		if(pessoa.getEmail() == null || pessoa.getEmail().isEmpty()){
//		if(StringUtils.isBlank(pessoa.getEmail())){
			throw new Exception("");
		}
	}


	private Pessoa listarPessoaPorNomeEmail(String nome,String email) {
		return pessoaRepository.listarPessoaPorNomeEmail(nome,email);
	}


	public void setLivroService(LivroService livroService) {
		this.livroService = livroService;
	}

	public void setPessoaRepository(IPessoaRepository pessoaRepository) {
		this.pessoaRepository = pessoaRepository;
	}

	public Pessoa getPessoa(Long idPessoa) {
		return pessoaRepository.loadById(idPessoa);
	}
}
