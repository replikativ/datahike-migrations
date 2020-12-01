# datahike migrations 

Manage datahike schema.

Based on a fork of [ragtime.datomic](https://github.com/hden/ragtime.datomic).

## Usage

First, add the following dependency to your project:

`[replikativ/datahike-migrations "0.1.0-SNAPSHOT"]`

Once you have at least one migration, you can set up Ragtime. You'll need to build a configuration map that will tell Ragtime how to connect to your database, and where the migrations are. In the example below, we'll put the configuration in the ragtime.sandbox namespace:

```clojure
(ns ragtime.sandbox
  (:require [ragtime.datomic :as rd]
            [ragtime.repl :as repl]
            [datahike.api :as d]))


(def cfg {:store  {:backend :mem :id "sandbox"}
          :keep-history? true
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
```


This library will install an extra schema inn your database.

```clojure
{:db/ident       :ragtime.datomic/migration-id
 :db/valueType   :db.type/keyword
 :db/unique      :db.unique/identity
 :db/cardinality :db.cardinality/one}
```

The following datum will be appended to each of the schema transactions.

```
[:db/add "datomic.tx" :ragtime.datomic/migration-id migration-id]
```

## License

Copyright © 2020 Chrislain Razafimahefa

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
