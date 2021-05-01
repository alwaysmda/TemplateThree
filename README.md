# TemplateThree
A Android template project that uses `Kotlin` `MVVM` `DataBinding` `OkHttp` `Room` `Kodein` and `BottomNavigationView`.

## Main Features
- Manage Fragments Easily
- Simple and Rich HTTP Request System
- Change Language in Runtime Easily
- Change and Customize Themes
- Easy Shared Element Transition
- Simple Fragments
- Simple RecyclerView Adapters
- Rich Extentions

## BaseActivity
There is only one activity and it is `BaseActivity`
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
__I'm tired now, lets write down the rest later :D__

