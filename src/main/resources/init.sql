create table tfc_sensor
(
    id_tr          integer  not null,
    gmt_event_time bigint   not null,
    n_num          integer not null,
    val            real     not null,
    gmt_sys_time   bigint   not null

);

insert into tfc_sensor(id_tr, gmt_event_time, n_num, val, gmt_sys_time) values (?,?,?,?,?);