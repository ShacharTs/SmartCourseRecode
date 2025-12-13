package com.smartcourse.ui.screens.user.tutor

//@HiltViewModel
//class TutorViewModel @Inject constructor(
//    private val userRepository: UserRepository,
//    private val chatRepository: ChatRepository
//) : ViewModel() {
//
//    private lateinit var currentUser: User
//
//    fun setUser(user: User) {
//        this.currentUser = user
//    }
//
//    private val _students = mutableStateOf<List<User>>(emptyList())
//    val students: State<List<User>> = _students
//
//    private val _recentChats = mutableStateOf<List<ChatItem>>(emptyList())
//    val recentChats: State<List<ChatItem>> = _recentChats
//
//    fun loadMyStudents() {
//        viewModelScope.launch {
//            // _students.value = userRepository.getStudentsForTutor(currentUser.getUID())
//        }
//    }
//
//    fun loadRecentChats() {
//        viewModelScope.launch {
//            _recentChats.value = userRepository.loadRecentChats(currentUser.getUID())
//        }
//    }
//
//    fun openChatWith(otherUserId: String, myNav: NavController) {
//        viewModelScope.launch {
//            //val chatId = chatRepository.ensureChat(currentUser.getUID(), otherUserId)
//            myNav.navigate(Screen.ChatRoom.route)
//        }
//    }
//}
