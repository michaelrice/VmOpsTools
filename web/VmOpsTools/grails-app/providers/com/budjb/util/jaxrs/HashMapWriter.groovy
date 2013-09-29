package com.budjb.util.jaxrs

import static org.grails.jaxrs.support.ConverterUtils.*
import static org.grails.jaxrs.support.ProviderUtils.*
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.converters.JSON
import grails.converters.XML
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider
import java.lang.reflect.Type
import java.lang.annotation.Annotation

/**
 * JaxRS message writer supporting hash maps.
 *
 * @author Bud Byrd
 * @web http://budjb.com/2012/02/24/jax-rs-and-hashmaps/
 */
@Provider
@Produces(['text/xml', 'application/xml', 'text/json', 'application/json'])
class HashMapWriter implements MessageBodyWriter<Object>
{
    GrailsApplication grailsApplication

    /**
     * Returns the size of the converted text.
     * We return -1 because we won't be figuring it out.
     *
     * @return Size of the converted text.
     */
    public long getSize(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        -1
    }

    /**
     * Determines if this writer supports the input and output types/objects.
     *
     * @return Whether this writer can do the conversion.
     */
    public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        if (!isXmlType(mediaType) && !isJsonType(mediaType)) {
            return false
        }

        HashMap.class.isAssignableFrom(type)
    }

    /**
     * Converts the input object to the requested output type.
     */
    public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream)
    {
        if (isXmlType(mediaType)) {
            def writer = new OutputStreamWriter(entityStream, getDefaultXMLEncoding(grailsApplication))
            def converter = new XML(t)
            converter.render(writer)
        }
        else {
            def writer = new OutputStreamWriter(entityStream, getDefaultJSONEncoding(grailsApplication))
            def converter = new JSON(t)
            converter.render(writer)
        }
    }
}