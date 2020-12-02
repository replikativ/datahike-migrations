(ns ragtime.sandbox
  (:require [ragtime.datomic :as rd]
            [ragtime.repl :as repl]
            [datahike.api :as d]))

(def cfg {:store  {:backend :mem :id "sandbox"}
          :keep-history? true
          ;; TODO: Make the full thing work when :write
          :schema-flexibility :read})

(d/delete-database cfg)

(def db (d/create-database cfg))

(def conn (d/connect cfg))

(def migration-1 (rd/create-migration :id [{:db/ident       :inv/sku
                                            :db/valueType   :db.type/string
                                            :db/unique      :db.unique/identity
                                            :db/cardinality :db.cardinality/one}]))

(def migration-2 (rd/create-migration :id-2 [{:db/ident       :inv/sku-2
                                              :db/valueType   :db.type/string
                                              :db/unique      :db.unique/identity
                                              :db/cardinality :db.cardinality/one}]))
;; At time 1
;;
(def config
  {:datastore  (rd/create-connection conn)
   :migrations [migration-1 migration-2]})


(repl/migrate config)

(d/datoms @conn {:index :eavt})


;; At time 2
;;
(def migration-3 (rd/create-migration :id-3 [{:db/ident       :inv/sku-3
                                              :db/valueType   :db.type/string
                                              :db/unique      :db.unique/identity
                                              :db/cardinality :db.cardinality/one}]))
(def config
  {:datastore  (rd/create-connection conn)
   :migrations [migration-1 migration-2 migration-3]})

(repl/migrate config)

(d/datoms @conn {:index :eavt})





(comment

  (do
    (def cfg {:store  {:backend :mem :id "sandbox"}
              :keep-history? true
              ;; TODO: Make the full thing work when :write
              :schema-flexibility :read})

    (d/delete-database cfg)

    (def db (d/create-database cfg))

    (def conn (d/connect cfg))

    (def migration-1 (rd/create-migration :id [{:db/ident       :inv/sku
                                              :db/valueType   :db.type/string
                                                :db/unique      :db.unique/identity
                                                :db/cardinality :db.cardinality/one}]))

    (def migration-2 (rd/create-migration :id-2 [{:db/ident       :inv/sku-2
                                                :db/valueType   :db.type/string
                                                :db/unique      :db.unique/identity
                                                :db/cardinality :db.cardinality/one}]))
    ;; At time 1
    ;;
    (def config
          {:datastore  (rd/create-connection conn)
           :migrations [migration-1 migration-2]})

    )

  (repl/migrate config)

  (d/datoms @conn {:index :eavt})


  ;; At time 2
  ;;
  (def migration-3 (rd/create-migration :id-3 [{:db/ident       :inv/sku-3
                                                :db/valueType   :db.type/string
                                                :db/unique      :db.unique/identity
                                                :db/cardinality :db.cardinality/one}]))
  (def config
    {:datastore  (rd/create-connection conn)
     :migrations [migration-1 migration-2 migration-3]})

  (repl/migrate config)

  (d/datoms @conn {:index :eavt})


  ;; At time-3 (adding 2 more shemas at once)
  (def migration-4-5 (rd/create-migration :id-4-5 [{:db/ident       :inv/sku-4
                                                :db/valueType   :db.type/string
                                                :db/unique      :db.unique/identity
                                                :db/cardinality :db.cardinality/one}
                                               {:db/ident       :inv/sku-5
                                                :db/valueType   :db.type/string
                                                :db/unique      :db.unique/identity
                                                :db/cardinality :db.cardinality/one}]))

  (def config
    {:datastore  (rd/create-connection conn)
     :migrations [migration-1 migration-2 migration-3 migration-4-5]})

  (repl/migrate config)

  (d/datoms @conn {:index :eavt})

  )
