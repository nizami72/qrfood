# Test Plan for Category.jsx Functionality

### **1. Testing Strategy**

We'll use a combination of testing strategies:

*   **Unit Tests:** For individual functions and hooks. We will mock dependencies like API services and other hooks to test logic in isolation.
*   **Component Tests:** To verify that individual React components render correctly given different props and states.
*   **End-to-End (E2E) Tests:** To simulate real user workflows from the UI. This will be the primary method for ensuring all parts work together correctly. We'll use a testing framework like Cypress or Playwright for this, describing the user stories.

### **2. Unit & Component Test Plan**

#### **`services/categoryService.js`**

*   **Objective:** Ensure each service function makes the correct API request.
*   **Method:** Use a mocking library (like Jest's `jest.mock`) to spy on the global `fetch` function.
*   **Test Cases:**
    *   `getCategories`: Verify it calls `fetch` with the correct URL (`/api/eatery/{restaurantId}/category`), `GET` method, and `Authorization` header.
    *   `createCategory`: Verify it calls `fetch` with the correct URL, `POST` method, `Authorization` header, and a `FormData` body containing the correct JSON and image data.
    *   `updateCategory`: Verify it calls `fetch` with the correct URL (`.../category/{categoryId}`), `PUT` method, and appropriate body.
    *   `deleteCategory`: Verify it calls `fetch` with the correct URL and `DELETE` method.
    *   `updateCategoryOrder`: Verify it calls `fetch` with the correct URL (`.../category/order`), `PUT` method, and a JSON body containing an array of category IDs.
    *   `getCommonCategories`: Verify it calls `fetch` with the `/api/category/common` URL.
    *   **Error Handling:** For each function, test that it correctly throws an error when the `fetch` response is not `ok`.

#### **`hooks/useCategories.js`**

*   **Objective:** Ensure the hook manages state correctly based on API responses.
*   **Method:** Mock the entire `categoryService` module.
*   **Test Cases:**
    *   **Initial State:** Test that the hook initializes with `loading: true`, `categories: []`, and `error: null`.
    *   **Fetch Success:**
        *   Mock `categoryService.getCategories` to return a sample array of categories.
        *   Verify that after the hook runs, `loading` becomes `false` and `categories` is populated with the sample data.
    *   **Fetch Failure:**
        *   Mock `categoryService.getCategories` to throw an error.
        *   Verify `loading` becomes `false` and `error` contains the error message.
    *   **`addCategory`:**
        *   Mock `categoryService.createCategory` to return a new category object.
        *   Call `addCategory` and verify the new category is appended to the `categories` state array.
    *   **`updateCategory`:**
        *   Mock `categoryService.updateCategory` to return an updated category object.
        *   Call `updateCategory` and verify the correct category in the `categories` array is replaced.
    *   **`deleteCategory`:**
        *   Call `deleteCategory` with a specific `categoryId`.
        *   Verify the corresponding category is removed from the `categories` state array.
    *   **`reorderCategories`:**
        *   Call `reorderCategories` with a new array order.
        *   Verify the `categories` state is updated optimistically.
        *   Test the rollback mechanism if the mocked `categoryService.updateCategoryOrder` throws an error.

### **3. End-to-End (E2E) Test Plan**

*   **Objective:** Simulate user workflows to ensure the entire feature works as expected.
*   **Tool:** A framework like Cypress or Playwright.
*   **Setup:** Before each test suite, the application should be in a known state (e.g., user logged in, a restaurant exists). API calls will be mocked at the network level.

#### **Test Suite: Viewing Categories**

*   **Test 1: Successful Display:**
    1.  Navigate to the category management page for a specific restaurant.
    2.  **Assert:** A loading indicator is visible initially.
    3.  **Assert:** After the API call resolves, the loading indicator disappears.
    4.  **Assert:** A list of category cards is displayed, showing their names and dish counts.
*   **Test 2: Empty State:**
    1.  Mock the `getCategories` API to return an empty array `[]`.
    2.  Navigate to the page.
    3.  **Assert:** The "No categories found" message is displayed.
*   **Test 3: Error State:**
    1.  Mock the `getCategories` API to return a 500 error.
    2.  Navigate to the page.
    3.  **Assert:** An error message is displayed.

#### **Test Suite: CRUD Operations**

*   **Test 4: Add a New Category (Manual)**
    1.  Click the `[id=add-category-button]`.
    2.  **Assert:** The "Add Category" form appears.
    3.  Fill in the `[id=category-name-en]` input with "New Test Category".
    4.  (Optional) Attach a file to the `[id=category-image-upload]` input.
    5.  Click the `[id=category-form-submit]` button.
    6.  **Assert:** The form disappears.
    7.  **Assert:** A new category card with the name "New Test Category" appears in the list.
*   **Test 5: Edit an Existing Category**
    1.  On an existing category card, click the `[id=edit-category-{id}]` button.
    2.  **Assert:** The "Edit Category" form appears, pre-filled with the category's data.
    3.  Change the `[id=category-name-en]` input to "Updated Test Category".
    4.  Click the `[id=category-form-submit]` button.
    5.  **Assert:** The form disappears.
    6.  **Assert:** The category card now displays the name "Updated Test Category".
*   **Test 6: Delete a Category**
    1.  On an existing category card, click the `[id=delete-category-{id}]` button.
    2.  **Assert:** A browser confirmation dialog appears.
    3.  Accept the confirmation.
    4.  **Assert:** The category card is removed from the list.

#### **Test Suite: Advanced Features**

*   **Test 7: Search/Filter Categories**
    1.  Ensure at least two categories exist: "Pizza" and "Salads".
    2.  Type "Pizza" into the `[id=category-search]` input.
    3.  **Assert:** Only the "Pizza" category card is visible.
    4.  **Assert:** The "Salads" category card is not visible.
*   **Test 8: Drag-and-Drop Reordering**
    1.  Ensure at least two categories exist, "First" and "Second".
    2.  Drag the "Second" category card and drop it onto the "First" category card.
    3.  **Assert:** The "Second" category now appears before the "First" category in the list.
    4.  **Assert:** The `updateCategoryOrder` API was called with the correct new order of IDs.
*   **Test 9: Navigate to Edit Dishes**
    1.  Click the `[id=edit-dishes-{id}]` button on a category card.
    2.  **Assert:** The URL changes to `/admin/menu` and includes the correct `categoryId` and `restaurantId` as query parameters.
