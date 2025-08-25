# Step-by-step plan to test UI flow for Category Management

---

## Pre-requisites

1. The application must be running.
2. The user must be logged in as an administrator.
3. A restaurant must be selected or created to manage categories.

---

## Test Cases

### 1. View Categories
- **Action:**  
  Navigate to the "Category Management" section in the admin panel (e.g., `/admin/restaurant/:restaurantId/category`).
- **Expected Result:**  
  A list of existing categories for the selected restaurant is displayed. Each category card should show:
    - Its name (in available languages)
    - Number of dishes it contains
    - "Edit" and "Delete" buttons  
      If no categories exist, a "No categories found" message should be displayed.

---

### 2. Create a New Category
- **Action:**  
  Click the "+ Add Category" button.
- **Expected Result:**  
  A form appears with input fields for:
    - Name (Azerbaijani)
    - Name (English)
    - Name (Russian)
    - Category Image
- **Action:**  
  Fill in at least one of the name fields (e.g., "Name (English)" with "New Category").  
  Optionally, upload an image for the category.  
  Click the "Add Category" button within the form.
- **Expected Result:**  
  The new category appears in the list. A success message might be displayed (if implemented). The form should clear or close.

---

### 3. Edit an Existing Category
- **Action:**  
  Locate an existing category and click its "Edit" button.
- **Expected Result:**  
  The form appears, pre-filled with the selected category’s details.
- **Action:**  
  Modify one or more fields (e.g., change "Name (English)" to "Updated Category").  
  Optionally, change the image.  
  Click the "Save Changes" button.
- **Expected Result:**  
  The category’s details are updated in the list. A success message might be displayed. The form should clear or close.

---

### 4. Delete a Category (without dishes)
- **Action:**  
  Locate a category with no dishes and click "Delete".
- **Expected Result:**  
  A confirmation dialog appears (e.g., "Are you sure you want to delete this category?").
- **Action:**  
  Click "OK" to confirm.
- **Expected Result:**  
  The category is removed from the list. A success message might be displayed.

---

### 5. Delete a Category (with dishes)
- **Action:**  
  Locate a category that contains one or more dishes and click "Delete".
- **Expected Result:**  
  A confirmation dialog appears, indicating the number of dishes (e.g., "This category contains X dishes. Are you sure you want to delete it?").
- **Action:**  
  Click "OK" to confirm.
- **Expected Result:**  
  The category is removed from the list. A success message might be displayed.

---

### 6. Cancel Deleting a Category
- **Action:**  
  Locate any category and click "Delete".
- **Expected Result:**  
  A confirmation dialog appears.
- **Action:**  
  Click "Cancel".
- **Expected Result:**  
  The category remains in the list, no changes made.

---

### 7. Attempt to Create a Category with Missing Information
- **Action:**  
  Click "+ Add Category".  
  Leave all name fields empty.  
  Click "Add Category".
- **Expected Result:**  
  An error message appears (e.g., "Please fill at least one language name").  
  The category is not created. The form remains open.

---

### 8. Search Categories
- **Action:**  
  Enter a search term in "Search categories" input (e.g., "Appetizers").
- **Expected Result:**  
  The category list filters dynamically to show only categories matching the term in any language.
- **Action:**  
  Clear the search term.
- **Expected Result:**  
  All categories are shown again.

---

### 9. Drag and Drop Reordering
- **Action:**  
  Click and drag a category card to a new position.
- **Expected Result:**  
  The category visually moves to the new position.
- **Action:**  
  Release the mouse button to drop it.
- **Expected Result:**  
  Categories reorder in the UI.  
  *(Note: backend order update may not be immediately visible unless the page refreshes or a success message appears)*

---
