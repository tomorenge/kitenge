import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    public static void main(String[] args) {

        staticFileLocation("/public");
        String layout = "templates/layout.vtl";

        ProcessBuilder process = new ProcessBuilder();
        Integer port;
        if (process.environment().get("PORT") != null) {
            port = Integer.parseInt(process.environment().get("PORT"));
        } else {
            port = 4567;
        }

        port(port);

        /******************************
         * LOGIN AND INDEX PAGES
         *****************************/

        //get main page
        get("/", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("template", "templates/index.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        /******************************
         * PRODUCT
         *****************************/

        //get add product form
        get("/addProduct", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("product", Product.all());
            model.put("userId",  request.session().attribute("userId"));
            model.put("kname",  request.session().attribute("kname"));
            model.put("template", "templates/addProductForm.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        //add a new product
        post("/addNewProduct", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            Kiosk kiosk = Kiosk.find(Integer.parseInt(request.queryParams("userId")));
            String inputtedUsername = request.queryParams("name");
            String price = request.queryParams("price");
            String imageUrl = request.queryParams("imageurl");

            request.session().attribute("name", inputtedUsername);
            request.session().attribute("price", price);
            request.session().attribute("imageurl", imageUrl);
            request.session().attribute("userId", kiosk.getId());
            model.put("name", inputtedUsername);
            model.put("price", price);
            model.put("imageurl", imageUrl);
            model.put("userId", kiosk.getId());

            Product newProduct = new Product(inputtedUsername, Integer.parseInt(price), imageUrl, kiosk.getId());
            newProduct.save();
            model.put("newProduct", newProduct);
            String url = String.format("/myKiosk", newProduct.getId());
            response.redirect(url);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        /******************************
         * kiosk
         *****************************/

        //add a new kiosk
        post("/addNewKiosk", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();

            Vendor vendor = Vendor.find(Integer.parseInt(request.queryParams("userId")));
            String inputtedKioskname = request.queryParams("kname");

            request.session().attribute("kname", inputtedKioskname);
//            request.session().attribute("userId", vendor.getId());
            model.put("kname", inputtedKioskname);
//            model.put("userId", vendor.getId());

            Kiosk newKiosk = new Kiosk(inputtedKioskname, vendor.getId());
            newKiosk.save();
            model.put("newKiosk", newKiosk);
            String url = String.format("/myKiosk", newKiosk.getId());
            response.redirect(url);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        /******************************
         * COMMON USER
         *****************************/

        /******************************
         * VENDOR
         *****************************/

        //get add vendor form
        get("/addVendor", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("vendors", Vendor.all());
            model.put("template", "templates/addVendorForm.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());

        //add a new vendor
        post("/addNewVendor", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();

            String inputtedUsername = request.queryParams("name");
            String role = request.queryParams("role");

            request.session().attribute("name", inputtedUsername);
            request.session().attribute("role", role);
            model.put("name", inputtedUsername);
            model.put("role", role);

            Vendor newVendor = new Vendor(inputtedUsername, role);
            newVendor.save();
            model.put("newVendor", newVendor);
            String url = String.format("/myKiosk", newVendor.getId());
            response.redirect(url);
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        //get all vendors
        get("/myKiosk", (request, response) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            request.session().maxInactiveInterval(600);
            model.put("v", Vendor.all());
            model.put("name",  request.session().attribute("name"));
            model.put("userId",  request.session().attribute("userId"));
            model.put("template", "templates/myKiosk.vtl");
            return new ModelAndView(model, layout);
        }, new VelocityTemplateEngine());


        /******************************
         * ADMIN
         *****************************/

    }
}
