package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import edu.nd.pmcburne.hwapp.one.ui.theme.*
import edu.nd.pmcburne.hwapp.one.ui.theme.BasketballTheme
import org.jetbrains.annotations.ApiStatus
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Duration


class MainActivity : ComponentActivity() {

    private val vm: MainActivityViewModel by viewModels {
        BasketballViewModelFactory(
            (applicationContext as BasketballApp).repo
        )
    }
//    private val vm: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasketballTheme() {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Basketball(
                        items = vm.games,
                        checked = vm.showMen,
                        onChecked = vm::updateShowMen,
                        datePicked = vm.datePicked,
                        onDateSelected = vm::updateSelectedDate,
                        onRefresh = vm::refresh,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

class MainActivityViewModel(private val repo: BasketballRepo): ViewModel() {

//    var draft by mutableStateOf(BucketItem())
//        private set
//
//    fun updateName(newName: String) {
//        draft = draft.copy(name = newName)
//    }
//
//    fun updateDueDate(newDueDate: LocalDate) {
//        draft = draft.copy(dueDate = newDueDate)
//    }
//
//    fun saveDraftToRepo() {
//        BucketRepo.bucketList.add(draft)
////        draft = BucketItem() // optional: reset after saving
//    }

    var showMen by mutableStateOf(true)
    var datePicked by mutableStateOf(Date())

    var games = mutableStateListOf<Game>()


    init {
        refresh()
    }



    fun updateShowMen(newShowMen: Boolean){
        showMen = newShowMen
        refresh()
    }

    fun updateSelectedDate(newDatePickedMillis: Long?){

        if (newDatePickedMillis != null){
            val timezoneAdjustedInstant = Date(newDatePickedMillis)
                .toInstant()
                .plus(5, ChronoUnit.HOURS)
            datePicked = Date(timezoneAdjustedInstant.toEpochMilli())
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repo.pullGamesFromRemote(showMen, datePicked)
            games.clear()
            val loaded = repo.getGamesFromLocal(showMen, datePicked)
            Log.d("Repo", "local returned ${loaded.size} games")
            games.addAll(loaded)
        }
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Basketball(
    items: List<Game>,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
    datePicked: Date,
    onDateSelected: (Long?) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {

    var refreshing by remember {mutableStateOf(false)}
    val datePickerState = rememberDatePickerState()
    var open by remember {mutableStateOf(false)}
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = modifier,
        state = pullRefreshState,
        isRefreshing = refreshing,
        onRefresh = {
            refreshing = true
            onRefresh()
            refreshing = false
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ){
                Text(
                    text = "${if (checked) "Men's" else "Women's"} Games",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = {open = true},
                ) {
                    Text(text = datePicked
                        .toInstant()
                        .atZone(ZoneId.of("America/New_York"))
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                        .toString())
                }
                if (open) {
                    DatePickerDialog(
                        onDismissRequest = { open = false },
                        confirmButton = {
                            TextButton(onClick = {
                                onDateSelected(datePickerState.selectedDateMillis)
                                open = false
                            }) {
                                Text("Ok")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {open = false}) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = checked,
                    onCheckedChange = onChecked
                )
            }

            HorizontalDivider(
                color = Black,
                thickness = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()

            )

            val formatter = DateTimeFormatter.ofPattern("h:mm a")

            LazyColumn {
                items(items) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Black)
                            .padding(16.dp)
                    ){
                        Row (Modifier.fillMaxWidth()){
                            Column(){
                                Text(item.gameStatus)
                                if (!(item.gameStatus == "pre" ||
                                        item.gameStatus == "FINAL")) {
                                    val startTime = LocalTime.parse(
                                        item.startTime.substringBeforeLast(" "),
                                        formatter
                                    )
                                    val minutesPassed = Duration.between(startTime, LocalTime.now())
                                    Text("${40 - minutesPassed.toMinutes()}")
                                }

                                Row (){
                                    Column() {
                                        Text("Home")
                                        Text(item.homeTeam)
                                        item.homeTeamScore?.let {
                                            Text(item.homeTeamScore.toString())
                                        }
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Column() {
                                        Text("Guest")
                                        Text(item.awayTeam)
                                        item.awayTeamScore?.let {
                                            Text(item.awayTeamScore.toString())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }



    }

}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    HWStarterRepoTheme {
//        Greeting("Android")
//    }
//}