package com.algaworks.brewer.controller;

import com.algaworks.brewer.Security.UsuarioSistema;
import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.Cervejas;
import com.algaworks.brewer.repository.Vendas;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.session.TabelaItensSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/vendas")
public class VendasController {

    @Autowired
    private TabelaItensSession itensVenda;

    @Autowired
    private Cervejas cervejas;

    @Autowired
    private CadastroVendaService cadastroVendaService;

    @Autowired
    private VendaValidator vendaValidator;

    @Autowired
    private Vendas vendasRepository;

    @Autowired
    private Mailer mailer;

    @GetMapping("/nova")
    public ModelAndView nova(Venda venda) {
        ModelAndView mv = new ModelAndView("venda/CadastroVenda");

        setUUID(venda);
        mv.addObject("itens", venda.getItens());
        mv.addObject("valorFrete", venda.getValorFrete());
        mv.addObject("valorDesconto", venda.getValorDesconto());
        mv.addObject("valorTotalItens", itensVenda.getValorTotal(venda.getUuid()));

        return mv;
    }

    @GetMapping
    public ModelAndView pesquisar(VendaFilter vendaFilter, BindingResult result
            , @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
        ModelAndView mv = new ModelAndView("venda/PesquisaVendas");
        mv.addObject("todosStatus", StatusVenda.values());

        PageWrapper<Venda> pagina = new PageWrapper<>(vendasRepository.filtrar(vendaFilter, pageable), httpServletRequest);
        mv.addObject("pagina", pagina);
        return mv;
    }

    @PostMapping(value = "/nova", params = "salvar")
    public ModelAndView salvar(Venda venda, RedirectAttributes attributes, BindingResult result, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
        validarVenda(venda, result, usuarioSistema);
        if (result.hasErrors()) {
            return nova(venda);
        }
        cadastroVendaService.salvar(venda);
        attributes.addFlashAttribute("mensagem", "Venda salva com sucesso");
        return new ModelAndView("redirect:/vendas/nova");
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        Venda venda = vendasRepository.buscarVendaComItens(codigo);

        setUUID(venda);
        for (ItemVenda itemVenda : venda.getItens()) {
            itensVenda.adicionarItem(venda.getUuid(), itemVenda.getCerveja(),itemVenda.getQuantidade());
        }
        ModelAndView mv = nova(venda);
        mv.addObject(venda);
        return mv;
    }

    private void setUUID(Venda venda) {
        if (StringUtils.isEmpty(venda.getUuid())) {
            venda.setUuid(UUID.randomUUID().toString());
        }
    }


    @PostMapping(value = "/nova", params = "emitir")
    public ModelAndView emitir(Venda venda, RedirectAttributes attributes, BindingResult result, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
        validarVenda(venda, result, usuarioSistema);
        if (result.hasErrors()) {
            return nova(venda);
        }

        cadastroVendaService.emitir(venda);
        attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso");
        return new ModelAndView("redirect:/vendas/nova");
    }

    @PostMapping(value = "/nova", params = "enviarEmail")
    public ModelAndView enviarEmail(Venda venda, RedirectAttributes attributes, BindingResult result, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
        validarVenda(venda, result, usuarioSistema);
        if (result.hasErrors()) {
            return nova(venda);
        }

        venda = cadastroVendaService.salvar(venda);
        mailer.enviar(venda);

        attributes.addFlashAttribute("mensagem", String.format("Venda n° %d salva e e-mail enviado sucesso",venda.getCodigo()));
        return new ModelAndView("redirect:/vendas/nova");
    }

    @PostMapping(value = "/nova", params = "cancelar")
    public ModelAndView cancelar(Venda venda, RedirectAttributes attributes, BindingResult result, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
        try {
            cadastroVendaService.cancelar(venda);

        } catch (AccessDeniedException e) {
            System.out.println(" erro aqui" + e.getMessage());
            return new ModelAndView("/403");
        }

        attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso");
        return new ModelAndView("redirect:/vendas/" + venda.getCodigo());
    }

    @PostMapping("/item")
    public @ResponseBody
    ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
        Cerveja cerveja = cervejas.getOne(codigoCerveja);
        itensVenda.adicionarItem(uuid, cerveja, 1);

        return getModelAndViewTabelaItens(uuid);
    }

    @PutMapping("/item/{codigoCerveja}")
    public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade, String uuid) {
        Cerveja cerveja = cervejas.getOne(codigoCerveja);
        itensVenda.alterarQuantidadeItens(uuid, cerveja, quantidade);

        return getModelAndViewTabelaItens(uuid);
    }


    @DeleteMapping("/item/{uuid}/{codigoCerveja}")
    public ModelAndView excluirItem(@PathVariable("codigoCerveja") Cerveja cerveja, @PathVariable String uuid) {
        itensVenda.excluirItem(uuid, cerveja);
        return getModelAndViewTabelaItens(uuid);
    }

    @GetMapping("/totalPorMes")
    public @ResponseBody List<VendaMes> listarTotalVendasPorMes(){
        return vendasRepository.totalPorMes();
    }

    @GetMapping("/porOrigem")
    public @ResponseBody List<VendaOrigem> listarTotalVendasPorOrigem(){
        return vendasRepository.totalPorOrigem();
    }

    private ModelAndView getModelAndViewTabelaItens(String uuid) {
        ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
        mv.addObject("itens", itensVenda.getItens(uuid));
        mv.addObject("valorTotal", itensVenda.getValorTotal(uuid));
        return mv;
    }

    private void validarVenda(Venda venda, BindingResult result, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
        venda.setUsuario(usuarioSistema.getUsuario());
        venda.adicionarItens(itensVenda.getItens(venda.getUuid()));
        venda.calcularValorTotal();
        vendaValidator.validate(venda, result);
    }
}
