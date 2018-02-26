package com.locassa.yamo.service;

import com.locassa.yamo.model.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
public class FreeMarkerService {

    private static final Logger logger = Logger.getLogger(FreeMarkerService.class);
    private Configuration cfg;

    public FreeMarkerService() throws Exception {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setClassForTemplateLoading(this.getClass(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    public String recoverPasswordHtmlContent(User user) {
        String html = "";
        try {
            Template template = cfg.getTemplate("recoverPasswordTemplate.html");
            if (null != template) {
                Map<String, String> root = new HashMap<String, String>();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(stream, Charset.forName("UTF-8"));
                root.put("secretcode", user.getSecretCode());
                template.process(root, out);
                html = stream.toString();
            }
        } catch (Exception e) {
            logger.error("Could not load template.", e);
        }

        return html;
    }

}
