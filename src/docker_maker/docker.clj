(ns docker-maker.docker
  (:require [clojure.java.io :as io])
  (:import (com.github.dockerjava.core DockerClientConfig DockerClientBuilder)
           (com.github.dockerjava.api DockerClient)
           (com.github.dockerjava.core.command BuildImageResultCallback)
           (java.io File ByteArrayOutputStream)
           (org.apache.commons.compress.archivers.tar TarArchiveEntry TarArchiveOutputStream)))

(defn docker-client []
  (let [config (.. DockerClientConfig createDefaultConfigBuilder build)]
    (.. DockerClientBuilder (getInstance config) build)))

(defn docker-info []
  (.. (docker-client) infoCmd exec))

(defn put-archive-entry
  "Adds an entry to a tar archive."
  [^TarArchiveOutputStream tar-output-stream filename content]
  (let [entry (doto (TarArchiveEntry. filename)
                (.setSize (count content)))]
    (doto tar-output-stream
      (.putArchiveEntry entry)
      (.write (.getBytes content))
      (.closeArchiveEntry))))

(defn build-docker-tar
  "Returns a docker context tar data in-memory,
  containing only a Dockerfile with the provided contents."
  [dockerfile-str]
  (let [buffer (ByteArrayOutputStream.)]
    (with-open [os (TarArchiveOutputStream. buffer)]
      (put-archive-entry os "Dockerfile" dockerfile-str))
    (.toByteArray buffer)))

(defn docker-build [dockerfile-str]
  (let [docker-context-istream (io/input-stream (build-docker-tar dockerfile-str))]
    (.. (docker-client)
        (buildImageCmd docker-context-istream)
        (exec (BuildImageResultCallback.))
        awaitImageId)))
