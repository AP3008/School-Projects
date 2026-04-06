#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "snackaroo.h"
#include "snackaroo_dish.h"
#include "snackaroo_driver.h"
#include "snackaroo_dish_driver.h"
#include "snackaroo_file_io.h"

/*
Function: dump_database
Description: Saves all dishes and drivers to a binary file.
Prompts user for filename and writes the data in binary format.
 */
int dump_database(struct DishNode *dish_head, 
                 struct DriverNode *driver_head) {
    char filename[256];
    
    printf("\tEnter filename to save database: ");
    scanf("%s", filename);
    clearInputBuffer();
    
    FILE *file = fopen(filename, "wb");
    
    if (file == NULL) {
        printf("\tError: Could not open file '%s' for writing\n", filename);
        return 0;
    }
    
    // Count dishes
    int num_dishes = 0;
    struct DishNode *dish_counter = dish_head;
    while (dish_counter != NULL) {
        num_dishes++;
        dish_counter = dish_counter->next;
    }
    
    // Count drivers
    int num_drivers = 0;
    struct DriverNode *driver_counter = driver_head;
    while (driver_counter != NULL) {
        num_drivers++;
        driver_counter = driver_counter->next;
    }
    
    printf("\tSaving %d dishes, %d drivers...\n", num_dishes, num_drivers);
    
    // Write dishes
    fwrite(&num_dishes, sizeof(int), 1, file);
    struct DishNode *curr_dish = dish_head;
    while (curr_dish != NULL) {
        fwrite(&(curr_dish->dish), sizeof(struct Dish), 1, file);
        curr_dish = curr_dish->next;
    }
    
    // Write drivers
    fwrite(&num_drivers, sizeof(int), 1, file);
    struct DriverNode *curr_driver = driver_head;
    while (curr_driver != NULL) {
        fwrite(&(curr_driver->driver), sizeof(struct Driver), 1, file);
        curr_driver = curr_driver->next;
    }
    
    fclose(file);
    printf("\tDatabase saved successfully to '%s'!\n", filename);
    return 1;
}

/*
Function: restore_database
Description: Loads dishes and drivers from a binary file.
Clears existing data before loading new data from file.
*/
int restore_database(struct DishNode **dish_head, struct DriverNode **driver_head) {
    char filename[256];
    
    printf("\tEnter filename to restore database: ");
    scanf("%s", filename);
    clearInputBuffer();
    
    FILE *file = fopen(filename, "rb");
    
    if (file == NULL) {
        printf("\tError: Could not open file '%s' for reading\n", filename);
        return 0;
    }
    
    // Clear existing data
    printf("\tClearing existing database...\n");
    
    struct DishNode *curr_dish = *dish_head;
    struct DishNode *next_dish;
    while (curr_dish != NULL) {
        next_dish = curr_dish->next;
        free(curr_dish);
        curr_dish = next_dish;
    }
    *dish_head = NULL;
    
    struct DriverNode *curr_driver = *driver_head;
    struct DriverNode *next_driver;
    while (curr_driver != NULL) {
        next_driver = curr_driver->next;
        free(curr_driver);
        curr_driver = next_driver;
    }
    *driver_head = NULL;
    
    int num_dishes, num_drivers;
    
    // Read dishes
    if (fread(&num_dishes, sizeof(int), 1, file) != 1) {
        printf("\tError: Failed to read dish count\n");
        fclose(file);
        return 0;
    }
    
    printf("\tRestoring %d dishes...\n", num_dishes);
    
    for (int i = 0; i < num_dishes; i++) {
        struct DishNode *new_node = (struct DishNode*)malloc(sizeof(struct DishNode));
        
        if (new_node == NULL) {
            printf("\tError: Memory allocation failed\n");
            fclose(file);
            return 0;
        }
        
        if (fread(&(new_node->dish), sizeof(struct Dish), 1, file) != 1) {
            printf("\tError: Failed to read dish data\n");
            free(new_node);
            fclose(file);
            return 0;
        }
        
        new_node->next = NULL;
        
        if (*dish_head == NULL) {
            *dish_head = new_node;
        } else {
            struct DishNode *curr = *dish_head;
            while (curr->next != NULL) {
                curr = curr->next;
            }
            curr->next = new_node;
        }
    }
    
    // Read drivers
    if (fread(&num_drivers, sizeof(int), 1, file) != 1) {
        printf("\tError: Failed to read driver count\n");
        fclose(file);
        return 0;
    }
    
    printf("\tRestoring %d drivers...\n", num_drivers);
    
    for (int i = 0; i < num_drivers; i++) {
        struct DriverNode *new_node = (struct DriverNode*)malloc(sizeof(struct DriverNode));
        
        if (new_node == NULL) {
            printf("\tError: Memory allocation failed\n");
            fclose(file);
            return 0;
        }
        
        if (fread(&(new_node->driver), sizeof(struct Driver), 1, file) != 1) {
            printf("\tError: Failed to read driver data\n");
            free(new_node);
            fclose(file);
            return 0;
        }
        
        new_node->next = NULL;
        
        if (*driver_head == NULL) {
            *driver_head = new_node;
        } else {
            struct DriverNode *curr = *driver_head;
            while (curr->next != NULL) {
                curr = curr->next;
            }
            curr->next = new_node;
        }
    }
    
    fclose(file);
    printf("\tDatabase restored successfully from '%s'!\n", filename);
    printf("\tLoaded: %d dishes, %d drivers\n", num_dishes, num_drivers);
    return 1;
}
    /*
    Function: dump_relationships
    Description: Saves all dish/driver relationships to a text file.
    Each line contains a dish code and driver code pair.
    */
int dump_relationships(struct DishDriverCode *rel_head) {
    char filename[256];
    
    printf("\tEnter filename to save relationships: ");
    scanf("%s", filename);
    clearInputBuffer();
    
    FILE *file = fopen(filename, "w");
    
    if (file == NULL) {
        printf("\tError: Could not open file '%s' for writing\n", filename);
        return 0;
    }
    
    int count = 0;
    struct DishDriverCode *curr = rel_head;
    
    while (curr != NULL) {
        fprintf(file, "%d %d\n", curr->dish_code, curr->driver_code);
        count++;
        curr = curr->next;
    }
    
    fclose(file);
    printf("\t%d relationships saved to '%s'!\n", count, filename);
    return 1;
}

/*
Function: restore_relationships
Description: Loads dish/driver relationships from a text file.
Validates that referenced dishes and drivers exist before creating relationships.
*/
int restore_relationships(struct DishDriverCode **rel_head, 
                         struct DishNode *dish_head, 
                         struct DriverNode *driver_head) {
    char filename[256];
    
    printf("\tEnter filename to restore relationships: ");
    scanf("%s", filename);
    clearInputBuffer();
    
    FILE *file = fopen(filename, "r");
    
    if (file == NULL) {
        printf("\tError: Could not open file '%s' for reading\n", filename);
        return 0;
    }
    
    // Clear existing relationships
    printf("\tClearing existing relationships...\n");
    struct DishDriverCode *curr = *rel_head;
    struct DishDriverCode *next;
    while (curr != NULL) {
        next = curr->next;
        free(curr);
        curr = next;
    }
    *rel_head = NULL;
    
    int dish_code, driver_code;
    int count = 0;
    int skipped = 0;
    
    while (fscanf(file, "%d %d", &dish_code, &driver_code) == 2) {
        // Validate that dish exists
        if (!dish_code_exists(dish_code, dish_head)) {
            printf("\tWarning: Dish code %d does not exist. Skipping relationship.\n", dish_code);
            skipped++;
            continue;
        }
        
        // Validate that driver exists
        if (!driver_code_exists(driver_code, driver_head)) {
            printf("\tWarning: Driver code %d does not exist. Skipping relationship.\n", driver_code);
            skipped++;
            continue;
        }
        
        // Create new relationship
        struct DishDriverCode *new_node = (struct DishDriverCode*)malloc(sizeof(struct DishDriverCode));
        
        if (new_node == NULL) {
            printf("\tError: Memory allocation failed\n");
            fclose(file);
            return 0;
        }
        
        new_node->dish_code = dish_code;
        new_node->driver_code = driver_code;
        new_node->next = NULL;
        
        // Add to end of list
        if (*rel_head == NULL) {
            *rel_head = new_node;
        } else {
            struct DishDriverCode *curr = *rel_head;
            while (curr->next != NULL) {
                curr = curr->next;
            }
            curr->next = new_node;
        }
        
        count++;
    }
    
    fclose(file);
    printf("\t%d relationships restored from '%s'!\n", count, filename);
    if (skipped > 0) {
        printf("\t%d relationships were skipped due to errors.\n", skipped);
    }
    return 1;
}
