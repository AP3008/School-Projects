#ifndef DISH_DRIVER_H
#define DISH_DRIVER_H

#include "snackaroo.h"

// Helper functions used in every file
struct DishNode* find_dish_by_code(struct DishNode* head, int dishCode);
struct DriverNode* find_driver_by_code(struct DriverNode* head, int driverCode);
int relationship_exists(struct DishDriverCode* rel_head, int dishCode, int driverCode);

// Relationship specific functions
int insert_relationship(struct DishDriverCode** rel_head, struct DishNode* dish_head, struct DriverNode* driver_head);

void search_relationship(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head);

void print_dishes_for_driver(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head);

void print_drivers_for_dish(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head);

void print_all(struct DishDriverCode *rel_head, struct DishNode *dish_head, struct DriverNode *driver_head); 

int erase_relationship(struct DishDriverCode **rel_head);

#endif
