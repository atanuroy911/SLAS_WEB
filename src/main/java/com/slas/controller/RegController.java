package com.slas.controller;

import com.slas.entity.TeacherAuthEntity;
import com.slas.repo.RepoSlas;
import com.slas.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


/**
 * @author hxc
 * @version 1.0
 * @date 2019/11/4 10:59
 */

@RestController
public class RegController{
    @Autowired
    RepoSlas repoSlas;

    @PostMapping("/Register")
    public ModelAndView register(TeacherAuthEntity teacherAuthEntity ,Model model){

        System.out.println(teacherAuthEntity.toString());
        if (teacherAuthEntity.getAdmin_pwd().equals("991211")) {
            teacherAuthEntity.setPwd_no(MD5Util.inputPassToFormPass(teacherAuthEntity.getPwd_no()));
            teacherAuthEntity.setAdmin_pwd("0");

            repoSlas.save(teacherAuthEntity);
//            System.out.println(teacherAuthEntity.toString());
//            System.out.println(MD5Util.formPassToDBPass(teacherAuthEntity.getPwd_no(),"1a2b3c4d"));


            model.addAttribute("msg","Registration Complete!!");
            return new ModelAndView("login");

        } else if (!teacherAuthEntity.getAdmin_pwd().equals("991211")){
            model.addAttribute("msg","Incorrect Combination of Passwords!! Try Again!!");
            return new ModelAndView("signup");
        }
        else {
            return new ModelAndView("error");
        }

    }


//    @GetMapping("/by/{id}")
//    public TeacherAuthEntity fndall(@PathVariable("id") Integer id){
//        return repoSlas.findById(id).orElse(null);
//
//    }


}
