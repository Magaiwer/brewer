package com.algaworks.brewer.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/estados")
public class EstadosController {

    public ModelAndView buscar(){
        ModelAndView mv = new ModelAndView("estados/CadastroEstados");
        return mv;
    }

}
