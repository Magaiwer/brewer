package com.algaworks.brewer.controller;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.Grupos;
import com.algaworks.brewer.repository.Usuarios;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.StatusUsuario;
import com.algaworks.brewer.service.exception.EmailJaCadastradoException;
import com.algaworks.brewer.service.exception.SenhaUsuarioObrigatoriaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private CadastroUsuarioService usuarioService;

    @Autowired
    private Grupos grupos;

    @Autowired
    private Usuarios usuarios;

    @RequestMapping("/novo")
    public ModelAndView novo(Usuario usuario) {
        ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
        mv.addObject("grupos", grupos.findAll());
        return mv;
    }

    @PostMapping({"/novo","{\\d+}"})
    public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return novo(usuario);
        }
        try {
            usuarioService.salvar(usuario);

        } catch (EmailJaCadastradoException e) {
            result.rejectValue("email", e.getMessage(), e.getMessage());
            return novo(usuario);
        } catch (SenhaUsuarioObrigatoriaException e) {
            result.rejectValue("senha", e.getMessage(), e.getMessage());
            return novo(usuario);
        }
        attributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso");
        return new ModelAndView("redirect:/usuarios/novo");
    }

    @GetMapping
    public ModelAndView pesquisar(UsuarioFilter usuarioFilter, BindingResult result
            , @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
        ModelAndView mv = new ModelAndView("/usuario/PesquisaUsuarios");
        mv.addObject("grupos", grupos.findAll());

        PageWrapper<Usuario> paginas = new PageWrapper<>(usuarios.filter(usuarioFilter, pageable), httpServletRequest);
        mv.addObject("paginas", paginas);
        return mv;
    }

    @PutMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public void atualizarStatus(@RequestParam("codigos[]") Long codigos[],@RequestParam("status") StatusUsuario statusUsuario ){
        usuarioService.alterarStatus(codigos,statusUsuario);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo){
        Usuario usuario = usuarios.buscarUsuarioComGrupos(codigo);
        ModelAndView mv = novo(usuario);
        mv.addObject(usuario);
        return mv;
    }
}
