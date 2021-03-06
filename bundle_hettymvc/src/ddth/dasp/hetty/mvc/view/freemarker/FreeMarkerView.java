package ddth.dasp.hetty.mvc.view.freemarker;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import ddth.dasp.hetty.message.IRequest;
import ddth.dasp.hetty.message.IResponse;
import ddth.dasp.hetty.message.protobuf.ResponseUtils;
import ddth.dasp.hetty.mvc.view.IView;
import ddth.dasp.hetty.qnt.ITopicPublisher;
import freemarker.template.Template;

public class FreeMarkerView implements IView {

    private Template template;
    private String encoding = "UTF-8";
    private String contentType = "text/html; charset=utf-8";

    public FreeMarkerView() {
    }

    public FreeMarkerView(Template template) {
        setTemplate(template);
    }

    public FreeMarkerView setTemplate(Template template) {
        this.template = template;
        return this;
    }

    protected Template getTemplate() {
        return template;
    }

    public FreeMarkerView setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    protected String getEncoding() {
        return encoding;
    }

    public FreeMarkerView setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    protected String getContentType() {
        return contentType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(IRequest request, Object model, ITopicPublisher topicPublisher,
            String topicName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(baos, encoding);
        template.process(model, out);
        out.flush();
        byte[] content = baos.toByteArray();
        IResponse response = ResponseUtils.response200(request, content, getContentType());
        topicPublisher.publish(topicName, response);
    }
}
