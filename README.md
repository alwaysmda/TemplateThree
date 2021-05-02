# TemplateThree
An Android template project that uses `Kotlin` `MVVM` `DataBinding` `OkHttp` `Room` `Kodein` and `BottomNavigationView`.

## Main Features
- Manage Fragments Easily
- Simple and Rich HTTP Request System
- Change Language in Runtime Easily
- Change and Customize Themes
- Easy Shared Element Transition
- Simple Fragments
- Simple RecyclerView Adapters
- Rich Extentions

## ApplicationClass
This is the `Application` class and provides the following features:

### Change Language in Runtime
To change application language, call `changeLang` and pass the `Languages` enum value to the function.
Changing language in different versions of android is a headache. So I created a new system to change language.
Intead of `strings.xml` file, follow the example and add all strings to `ApplicationClass`.
All classes and also layouts have access to `ApplicationClass`.

### Change Theme in Runtime
To change applicatio theme, call `changeTheme` and pass the `Themes` enum value to the function.
Available themes are added in `styles.xml` and you can set different color for anything you want.
Also there is an enum for each theme.
Please note that having `Dark` in theme name changes the application theme to dark mode.

### Excrypted Shared Preferences
call `setPref` to save a value and call `getStringPref`, `getBooleanPref`, `getIntPref`, `getLongPref` or `getFloatPref` to get the saved value.

### Fonts
There are some fonts initialized in `ApplicationClass` and can be accessed anywhere in classes and layouts.
However its best to access fonts using `@font/...` in xmls.

### Universal RecyclerView Animation
There is a `LayoutAnimationController` instance initialized and can be used like `android:layoutAnimation="@{appClass.recyclerViewAnimation}"` in xmls.

## BaseActivity
There is only one activity and it is `BaseActivity`.
It controls the fragments and the navigation view.

### Options:
**StartMode**
- SingleInstance: Only one instance of each fragment can be created in each tab.
- MultiInstance: Multiple instances of a fragment can be created.

**ExitMode**
- Normal: After reaching the base fragment in a tab, pressing back button will exit the application.
- BackToFirstTab: After reaching the base fragment in a tab, if it is not the first tab, it returns to fist tab.

**TransitionAnimation**
The animation of switching between fragments.
Possible values are:
- TRANSIT_FRAGMENT_FADE
- TRANSIT_FRAGMENT_OPEN
- TRANSIT_FRAGMENT_CLOSE
- TRANSIT_NONE

### Important Functions:
#### setLoading
Shows a `ProgressBar` and disables touch.

#### start
Starts a new fragment.

#### selectTab
Navigate between navigation tabs.

#### initFragmentTable
Call this method and create the initial fragments that will load on each tabs.

#### showHideNavigationBar
Shows/Hides the navigation bar.

## BaseFragment
All fragments inherit this fragment.
You must call `initialize` in `onAttach` of fragments and pass the layout id and the viewmodel's class.
It has the following variables:
- `appClass` : An instance of `ApplicationClass` that can be used as `Context`
- `viewModel` : An instance of your fragment's ViewModel
- `baseActivity` : An instance of `BaseActivity`
- `binding` : An instance of your `ViewDataBinding` that can be used to access view

### Important Functions:
#### rebind
Refreshes the view's data

#### grantPermission
Grant permissions using this method and result will be posted by `OnRequestPermissionResultEvent` event.

#### addSharedElement
Call a event to your fragment and add shared element views right before starting a new fragment.
Views don't need to have `transitionName`.
You only need to add the view you want to animate and the `id` of the target view.

## BaseAdapter:
All adapters inherit this adapter.
This is a generic adapter that holds a list of objects and has the option of showing a loading and an add button.
Unless the `BaseActivity` and `BaseFragment`, this class should be modified based on your project needs.
It has the following variables:
- `showAdd` : Shows an Add button at the beginning
- `showLoading` : Shows a `ProgressBar` as loading at the end
- `sectionIndex` : It is used to differenciate adapters in a nested list
- `addIsLeft` : Whether you want to show the add button on the left or not
- `isHorizontal` : Whether your list is horizontal or not
- `list` : A list of generic objects that bind into rows
- `bindList` : A list of pairs that contain the `position` and the `ViewDataBinding` of rows

In `onCreateViewHolder` the `viewType` variable is the position of the item in list.
`super` must be called for positions below zero, to handle loading and add button.
For the positions of zero and above, `ViewDataBinding` of the row must be created and also `bindList` should be modified just like the example.
Create a `static` view holder class and inherit the `BaseViewHolder` and pass variables of your needs.


__I'm tired now, lets write down the rest later :D__

