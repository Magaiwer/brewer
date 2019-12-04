package com.algaworks.brewer.controller;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.Cidades;
import com.algaworks.brewer.repository.Estados;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CadastroCidadeService;
import com.algaworks.brewer.service.exception.CidadeJaCadastradoException;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/cidades")
public class CidadesController {

    @Autowired
    private Cidades cidades;

    @Autowired
    private Estados estados;

    @Autowired
    private CadastroCidadeService cidadeService;

    @RequestMapping("/nova")
    public ModelAndView nova(Cidade cidade) {
        ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
        mv.addObject("estados", estados.findAll());
        return mv;
    }

    @PostMapping("/nova")
    @CacheEvict(value = "cidades",key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
    public ModelAndView cadastrar(@Valid Cidade cidade, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return nova(cidade);
        }
        try {

            cidadeService.salvar(cidade);
        }catch (CidadeJaCadastradoException e){
            result.rejectValue("nome",e.getMessage(),e.getMessage());
            return nova(cidade);
        }

        attributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
        return new ModelAndView("redirect:/cidades/nova");
    }

    @Cacheable(value = "cidades", key = "#codigoEstado")
    @RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
            @RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
        return cidades.findByEstadoCodigo(codigoEstado);
    }

    @GetMapping
    public ModelAndView filtrar(CidadeFilter cidadeFilter, @PageableDefault(size = 5)Pageable pageable, HttpServletRequest request){
        ModelAndView mv = new ModelAndView("cidade/PesquisaCidades");

        PageWrapper<Cidade> pagina = new PageWrapper<>(cidades.filtrar(cidadeFilter,pageable),request);
        mv.addObject("estados",estados.findAll());
        mv.addObject("pagina",pagina);
        return mv;
    }
}
