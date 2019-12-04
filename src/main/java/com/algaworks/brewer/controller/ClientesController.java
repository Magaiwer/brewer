package com.algaworks.brewer.controller;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.Clientes;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.exception.CpfCnpjJaCadastradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

    @Autowired
    private Estados estados;

    @Autowired
    private CadastroClienteService clienteService;

    @Autowired
    private Clientes clientes;

    @RequestMapping("/novo")
    public ModelAndView novo(Cliente cliente) {
        ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
        mv.addObject("tiposPessoa", TipoPessoa.values());
        mv.addObject("estados", estados.findAll());
        return mv;
    }

    @PostMapping("/novo")
    public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return novo(cliente);
        }
        try {
            clienteService.salvar(cliente);
        } catch (CpfCnpjJaCadastradoException e) {
            result.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
            return novo(cliente);
        }
        attributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");
        return new ModelAndView("redirect:/clientes/novo");
    }

    @GetMapping
    public ModelAndView pesquisar(ClienteFilter clienteFilter, @PageableDefault(size = 5) Pageable pageable, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("/cliente/PesquisaClientes");

        PageWrapper<Cliente> pagina = new PageWrapper<>(clientes.filtrar(clienteFilter, pageable), request);

        mv.addObject("pagina", pagina);
        return mv;
    }

    @RequestMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<Cliente> pesquisar(String nome){
        validarTamanhoNome(nome);
        return clientes.findByNomeStartingWithIgnoreCase(nome);

    }

    private void validarTamanhoNome(String nome) {
        if(StringUtils.isEmpty(nome) || nome.length() < 3){
            throw new IllegalArgumentException();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.<Void>badRequest().build();
    }



}
