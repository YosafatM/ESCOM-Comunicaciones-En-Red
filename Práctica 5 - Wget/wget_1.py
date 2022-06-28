#!/usr/bin/python3
import re
import requests
import threading
from concurrent.futures import ThreadPoolExecutor
from os import mkdir, chdir, makedirs
from os.path import basename, realpath
from urllib.parse import urlsplit
from urllib.request import urlopen, urlretrieve

from bs4 import BeautifulSoup


def get_tipo_contenido(sitio):
    try:
        r = requests.get(sitio)
        return r.headers['content-type']
    except Exception as e:  # Este problema puede ocurrir al descargar un archivo que no tenga extensión
        print("[W/TIPOCONT] Hay algo raro con este enlace! (", sitio, ")", sep="")
        return "unknown"  # Regresar algo por lo menos


# Esta función se encarga de obtener el tipo de codificación en la cual está el sitio
def get_codificacion(sitio):
    try:
        return requests.get(sitio).encoding  # Ya sea utf-8, o ISO, por nombrar
    except Exception as e:
        print("[ERROR/codificación] No se pudo obtener la codificación (", e, ")", sep="")


def descargar_sitio(sitio, nombre):
    try:
        urlab = urlopen(sitio).url  # En toda esta sección,se obtiene la ruta donde
        realp = realpath(__file__)  # el script se está ejecutando, y nos movemos a ese directorio
        realp = realp.replace(basename(realp), "")
        chdir(realp)  # Nos movemos a donde se ejecuta el Script
        uri = URI_real(sitio)  # Obtiene sólo el URI del sitio
        novositio = sitio.replace(uri, "")  # Obtener la estructura de carpetas

        try:
            makedirs(novositio)  # Trata de crear la estructura (subdirectorios)
        except:
            pass

        chdir(novositio)  # Nos movemos a dicha carpeta
        urlretrieve(urlab, nombre + ".html")  # Descargar el sitio
        chdir(realp)  # Regresamos al directorio del Script
        return nombre + ".html [ok]"
    except Exception as e:
        print("[ERROR/htmlDESCARGA] No es nuestro día de suerte (", e, ")", sep="")
        return False


def descargar_archivo(sitio):
    try:
        urlab = urlopen(sitio).url
        realp = realpath(__file__)
        realp = realp.replace(basename(realp), "")
        chdir(realp)  # Nos movemos al directorio donde se ejecuta el Script
        uri = URI_real(sitio)  # Obtiene solo el URI del sitio
        nombre_archivo = basename(urlab)  # Obtiene el nombre del archivo

        # Obtiene la estructura de carpetas (sin el http:// y sin el nombre de archivo)
        novositio = sitio.replace(uri, "").replace(nombre_archivo, "")

        try:
            makedirs(novositio)  # Crea los subdirectorios
        except:
            pass

        chdir(novositio)  # Nos movenos a dicha carpeta
        urlretrieve(urlab, nombre_archivo)  # Descargar el archivo
        chdir(realp)  # Regresamos al del Script
        return nombre_archivo + " [OK]"
    except Exception as e:
        print("[ERROR/DESCARGA] No es nuestro día de suerte (", e, ")", sep="")
        return False


# Validar que la url empiece con http(s) y que sea válida también
def validar_url(sitio):
    regex = re.compile(
        r'^(?:http|ftp)s?://'  # http:// o https://
        r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|'  # dominios
        r'localhost|'  # localhost...
        r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})'  # ...ip
        r'(?::\d+)?'  # optional port
        r'(?:/?|[/?]\S+)$', re.IGNORECASE)

    if re.match(regex, sitio):
        return True  # Si la url es válida, regresar True
    else:
        return False  # En otro caso, False


def abrir_sitio(sitio):
    if sitio.startswith("ftp://"):
        print("[ERROR] FTP no soportado. Saliendo")
        return None
    try:
        website = urlopen(sitio)  # Tratar de abrir el sitio
    except Exception as e:
        print("[ERROR/APERTURA] No es nuestro día de suerte (", e, ")", sep="")
        return None
    try:
        return website.read().decode(get_codificacion(sitio))
    except TypeError as e:
        print("[W/apertura] No hay nada para decodificar (", sitio, ")", sep="")


# Esta función se encarga de sacar el URI real
def URI_real(sitio):
    return "{0.scheme}://{0.netloc}/".format(urlsplit(sitio))


# Arregla enlaces que empiezan con //, o aquellos que sólo son enlaces a algo vacío (#,https://,http://)
# >>> fixhttp://aaa.com/asda/asdasd
# >>> aaa.com
def fix_enlace(sitios):
    nuevo = []
    for item in sitios:
        if item.startswith("//"):
            nuevo.append(item.replace('//', ''))
        elif item in ["#", "https://", "ftp://", "http://", "/"] or item.startswith("{{"):
            pass
        else:
            nuevo.append(item)
    return nuevo


# Esta función agrega el "https://" a algunos sitios que carecen de esto, como es el caso de por ejemplo abc.com
def concat_prefijo(sitios):
    nuevo = []
    for item in sitios:
        if item.startswith("http"):
            nuevo.append(item)  # Agregar tal cual si el sitio empieza con http (incluye https también)
        elif item.startswith("data:"):
            pass  # Si el URL es un archivo de datos (blob/b64) omitir, pues son cosas dinámicas
        else:
            nuevo.append("https://" + item)  # Agregarle el "https://" en caso de que no sean los casos anterioresl
    return nuevo


def fix_subenlace(sitios, uri):
    return [(uri + item[1:]) if (item.startswith("/")) else item for item in sitios]


# Esta función elimina el "/" que algunos sitios utilizan para recursos y les pega el URI al principio para que
# funcionen correctamente al descargar.
def concat_sitio_padre(listaSitios, uri):
    return [item for item in listaSitios if (item.startswith(uri))]


# Esta función regresa sólo aquellos enlaces que formen parte del "sitio padre", es decir, no incluye dominios que
# sean utilizados como CDNs o servidores de anuncios.
def get_nombre_pagina(sopa):
    return sopa.title.string.strip()  # Título de la página


# Esta función se encarga de arreglar aquellos enlaces que sólo son, por ejemplo 'documentos/', es decir,
# que no empiezan con "/"
def fix_diagonal(lista_sitios, uri):
    nuevo = []

    for item in lista_sitios:
        if validar_url(item):
            nuevo.append(item)
        else:
            if uri.endswith("/") or item.startswith("/"):
                nuevo.append(uri + item)
            else:
                nuevo.append(uri + "/" + item)
    return nuevo


def manejar_url(links, base):
    newArreglo = []
    print(links)
    for link in links:
        if len(link) > len(base):
            if '?' not in link:
                newArreglo.append(link)
    for link in newArreglo:
        process_thread = threading.Thread(target=wpyget, args=(link,))
        process_thread.start()
        process_thread.join()


def wpyget(sitio):
    if validar_url(sitio):                          # Realizar una validación al principio
        html = abrir_sitio(sitio)                   # Código html (texto plano)

        if html is None:                            # Cuando no hay cuerpo de html, es un archivo/recurso
            print("[DESCARGANDO]", descargar_archivo(sitio))
            exit(0)                                 # En caso de que haya algún error
        uri = URI_real(sitio)                       # Obtener URL real

        try:
            sopa = BeautifulSoup(html, "lxml")      # Convertimos el html a objeto de BeautifulSoup
        except Exception as e:
            print("Error al leer la página! Use lxml en la línea 55: ", e)
        finally:
            if sopa:
                pass                                # En caso de que se haya podido abrir, no hacer nada
            else:                                   # Si no se pudo abrir, intentar abrir con cualquier motor
                sopa = BeautifulSoup(html)

        tituloPagina = re.sub('\W+', '', get_nombre_pagina(sopa))           # Eliminar símbolos raros de una cadena
        print("Título de la página:", tituloPagina.replace(" ", ""))        # imprimir el título de la página

        try:
            mkdir(tituloPagina)
        except:
            pass

        chdir(tituloPagina)
        print("[HTML/DESCARGANDO]", descargar_sitio(sitio, tituloPagina))

        links = [a.get('href') for a in sopa.find_all('a', href=True)]      # Sacar todos los enlaces (href)
        links_recursos = [a for a in sopa.find_all(src=True)]               # Sacar todos los recursos (href)

        # Recursos con expresiones regulares
        recursos = concat_sitio_padre(fix_subenlace(fix_enlace(re.findall('src="([^"]+)"', str(html))), uri), uri)

        # Convertir a cadena todos los recursos que se sacaron con BeautifulSoup
        nuevos_recursos = [str(i) for i in links_recursos]

        # Sacar todos los enlaces que se encontraron con BeautifulSoup
        recursos_soup = re.findall('src="([^"]+)"', str(nuevos_recursos))

        # Quitar basura que pueda estar en el html, y arreglar subenlaces
        linksexternos = concat_sitio_padre(fix_diagonal(fix_subenlace(fix_enlace(links), uri), sitio), uri)

        recursos = concat_prefijo(recursos)
        recursos_soup = concat_sitio_padre(fix_subenlace(fix_enlace(recursos_soup), uri), uri)
        set_recursos = set(recursos + recursos_soup)

        url_finales = []
        arrayImgFiles = ['tif', 'tiff', 'bmp', 'jpg', 'jpeg', 'gif', 'png']

        for recurso in set_recursos:
            # Estos son los recursos presentes en la página (aquellos que apuntan a SRC)
            print("[SRC/DESCARGANDO]", recurso, descargar_archivo(recurso))
        for i in linksexternos:
            # Si el tipo de contenido empieza con "application", se trata de algún archivo
            if "application" in get_tipo_contenido(i) or any(ext in i for ext in arrayImgFiles):
                print("[LINK/DESCARGANDO]", i, descargar_archivo(i))
            else:
                print("[LINK]", i)                                          # Enlaces que tenemos que seguir visitando
                url_finales.append(i)

        print("URI", uri)
        manejar_url(url_finales, sitio)


if __name__ == "__main__":
    try:
        executor = ThreadPoolExecutor(max_workers=2)
        pagina = "http://148.204.58.221/axel/aplicaciones/22-2/"
        wpyget(pagina)
    except:
        pass
