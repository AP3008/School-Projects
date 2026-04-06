#ifndef SNACKAROO_FILE_IO_H
#define SNACKAROO_FILE_IO_H

#include "snackaroo.h"

// Dump and restore dishes and drivers
int dump_database(struct DishNode *dish_head, struct DriverNode *driver_head);

int restore_database(struct DishNode **dish_head, struct DriverNode **driver_head);

// Dump and restore relationships

int dump_relationships(struct DishDriverCode *rel_head);

int restore_relationships(struct DishDriverCode **rel_head, struct DishNode *dish_head, struct DriverNode *driver_head);

#endif
