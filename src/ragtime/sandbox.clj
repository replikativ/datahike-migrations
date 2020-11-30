(ns ragtime.sandbox
  (:require [ragtime.datomic :as rd]
            [ragtime.repl :as repl]
            [datahike.api :as d]))

(comment

  (def cfg {:store  {:backend :mem :id "sandbox"}
            :keep-history? true
            :schema-flexibility :read})

  (d/delete-database cfg)

  (d/create-database cfg)

  (def conn (d/connect cfg))

  (def migration (rd/create-migration :id [:db/ident :inv/sku
                                           :db/valueType :db.type/string
                                           :db/unique :db.unique/identity
                                           :db/cardinality :db.cardinality/one]))

  (def config
    {:datastore  (rd/create-connection conn)
     :migrations [migration]})

  (repl/migrate config)
  )
