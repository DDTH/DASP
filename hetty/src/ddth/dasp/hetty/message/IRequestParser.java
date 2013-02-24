package ddth.dasp.hetty.message;

public interface IRequestParser {
    public String getAction(IRequest request);

    public String getModule(IRequest request);

    public String getPathParam(IRequest request, int index);

    public String getUrlParam(IRequest request, String name);
}
