package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value="")
    private String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    private String add(Model model) {
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");

        return "menu/add";
    }



    @RequestMapping(value="add", method = RequestMethod.POST)
    private String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("menu", menu);
            model.addAttribute("title", "Add Menu");

            return "menu/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    //TODO: Create handlers to remove menus, create "remove-menu" template

    @RequestMapping(value="view/{menuId}", method = RequestMethod.GET)
    private String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute("title", menu.getName());

        return "menu/view";
    }

    @RequestMapping(value="add-item/{menuId}", method = RequestMethod.GET)
    private String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu:" + menu.getName());

        return "menu/add-item";
    }

    @RequestMapping(value="add-item", method = RequestMethod.POST)
    private String addItem(Model model,
                           @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("title", "Add  item to menu: " + form.getMenu().getName());

            return "menu/add-item";
        }

        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();
    }

    //TODO: Add handlers to remove items, create "remove-items" template

}
