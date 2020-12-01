;; TODO: rename datomic to datahike
(ns ragtime.datomic
  (:require [ragtime.protocols :as ragtime-protocol]
            [datahike.api :as datahike]))

(defn- create-schema [index-key]
  [{:db/ident       index-key
    :db/valueType   :db.type/keyword
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one}])

(defn- has-ident? [db ident]
  ;; TODO: RESTORE
  true
  #_(contains? (datahike/pull db [:db/ident] ident #_{:eid ident :selector [:db/ident]})
    :db/ident))

(defn- schema-loaded? [db index-key]
  (has-ident? db index-key))

(defn- ensure-ragtime-schema [conn index-key]
  (let [db (datahike/db conn)
        schema (create-schema index-key)]
    (when-not (schema-loaded? db index-key)
      (datahike/transact conn {:tx-data schema}))))

(defn- find-migrations [db index-key]
  (let [tuples (datahike/q '[:find ?id ?t
                             :in $ ?a
                             :where [_ ?a ?id ?t]]
                 db index-key)]
    (into []
      (map first)
      (sort-by last tuples))))

(defrecord Connection [conn index-key]
  ragtime-protocol/DataStore
  (add-migration-id [_ id])
  (remove-migration-id [_ id])
  (applied-migration-ids [_]
    (ensure-ragtime-schema conn index-key)
    (let [db (datahike/db conn)]
      (find-migrations db index-key))))

(defn create-connection
  ([conn] (create-connection conn ::migration-id))
  ([conn index-key]
                                        ;{:pre [(and (satisfies? client-protocols/Connection conn) (keyword? index-key))]}
   (->Connection conn index-key)))

(defrecord Migration [id txs index-key]
  ragtime-protocol/Migration
  (id [_] id)
  (run-up! [_ {:keys [conn]}]
    (ensure-ragtime-schema conn index-key)
    (let [tx-data (into [[:db/add "datomic.tx" index-key id]]
                    txs)]
      (datahike/transact conn {:tx-data tx-data})))
  (run-down! [_ conn]))

(defn create-migration
  ([id txs] (create-migration id txs ::migration-id))
  ([id txs index-key]
   {:pre [(and (keyword? id)
            (sequential? txs)
            (> (count txs) 0))]}
   (->Migration id txs index-key)))
