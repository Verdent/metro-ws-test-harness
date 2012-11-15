
package jaxws.mtom.client;

import java.awt.Image;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8-b13579
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Hello", targetNamespace = "http://example.org/mtom")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Hello {


    /**
     * 
     * @param data
     */
    @WebMethod
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void echoData(
        @WebParam(name = "data", targetNamespace = "http://example.org/mtom/data", mode = WebParam.Mode.INOUT, partName = "data")
        Holder<byte[]> data);

    /**
     * 
     * @param image
     * @param photo
     */
    @WebMethod(operationName = "Detail")
    @RequestWrapper(localName = "Detail", targetNamespace = "http://example.org/mtom/data", className = "jaxws.mtom.client.DetailType")
    @ResponseWrapper(localName = "DetailResponse", targetNamespace = "http://example.org/mtom/data", className = "jaxws.mtom.client.DetailType")
    public void detail(
        @WebParam(name = "Photo", targetNamespace = "http://example.org/mtom/data", mode = WebParam.Mode.INOUT)
        Holder<byte[]> photo,
        @WebParam(name = "image", targetNamespace = "http://example.org/mtom/data", mode = WebParam.Mode.INOUT)
        Holder<Image> image);

}