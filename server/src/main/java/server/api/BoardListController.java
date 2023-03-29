package server.api;



import commons.Board;
import commons.BoardList;

import commons.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.BoardListRepository;
import server.database.BoardRepository;



@RestController
@RequestMapping("/api/boardlists")
public class BoardListController {

    /**
     * repo - the JpaRepository that is used for all items of type BoardList
     */
    private final BoardListRepository repo;

    @Autowired
    private BoardUpdateListener boardUpdateListener;

    /**
     * parentRepo - the JpaRepository that is used for all items of type Board. This repo is necessary
     * for retrieving the value of the parentBoard field when constructing a new BoardList
     */
    private final BoardRepository parentRepo;

    public BoardListController(BoardListRepository repo, BoardRepository parentRepo){
        this.repo = repo;
        this.parentRepo = parentRepo;
    }

    /**
     * Gets a BoardList by id
     * @param id - the id of the BoardList that has to be retrieved
     * @return a ResponseEntity containing the BoardList that was requested by id, or a badrequest error if id is invalid
     */
    @GetMapping("/{id}")
    ResponseEntity<BoardList> getById(@PathVariable("id") long id){
        if(id < 0 || !repo.existsById(id)){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());

    }

    /**
     * Creates a new BoardList as a child of the board for which the id is passed
     * @param boardId - the id of the board to which the BoardList should be added
     * @return a ResponseEntity containing the newly created BoardList or a badrequest error if id is invalid
     */
    @PostMapping("/new-boardlist/{boardId}")
    ResponseEntity<BoardList> getNewList(@RequestBody BoardList newList,@PathVariable("boardId") long boardId){
        if(boardId < 0 || !parentRepo.existsById(boardId)){
            return ResponseEntity.badRequest().build();
        }
        newList.setParentBoard(parentRepo.getById(boardId));
        BoardList addedList = repo.save(newList);
        boardUpdateListener.add(addedList.getParentBoard());
        return ResponseEntity.ok(addedList);
    }

    @PutMapping("/{id}/{newName}")
    public ResponseEntity<BoardList> renameList(@PathVariable("id") long id, @PathVariable("newName") String newName) {
        if(!repo.existsById(id))return ResponseEntity.badRequest().build();
        BoardList currentList = repo.findById(id).get();
        currentList.setName(newName);
        BoardList updatedList = repo.save(currentList);
        boardUpdateListener.add(updatedList.getParentBoard());
        return ResponseEntity.ok(updatedList);
    }

    @DeleteMapping("/{id}")
    public void deleteList(@PathVariable("id") long id) {
        try {
            Board board=repo.getById(id).getParentBoard();
            var lists=board.getLists();
            lists.remove(repo.getById(id));
            board.setLists(lists);
            repo.deleteById(id);
            boardUpdateListener.add(repo.getById(id).getParentBoard());
        }catch(IllegalArgumentException e){
            System.out.println("The id for deleteList cannot be null");
            e.printStackTrace();
        }
    }

    /**
     * Method that moves a card from one position (cardToMove) to a different one (newPos).
     * The method first removes the card and then add it to the correct position, so if we have boardlist with 3 cards(card0, card1, card2), to put card0 at the back
     * we would do a call with cardToMove=0 ad newPos=2.
     * @return a ResponseEntity that contains the modified boardlist or a badrequests error if the method fails.
     */
    @PutMapping("/move-card/{id}/{cardToMove}/{newPos}")
    public ResponseEntity<BoardList> reorderCards(@PathVariable("id") long id, @PathVariable("cardToMove") long cardToMove, @PathVariable("newPos") long newPos){
        if(!repo.existsById(id) || cardToMove <0 || newPos<0) {
            return ResponseEntity.badRequest().build();

        }
        BoardList boardlist=repo.findById(id).get();
        var lists=boardlist.getCardList();
        if(cardToMove >=lists.size() || newPos>=lists.size()) {
            return ResponseEntity.badRequest().build();
        }
        Card movedCard=lists.get((int) cardToMove);
        lists.remove(movedCard);
        lists.add((int) newPos,movedCard);
        boardlist.setCardList(lists);
        BoardList updatedBoardList = repo.save(boardlist);
        boardUpdateListener.add(updatedBoardList.getParentBoard());
        return ResponseEntity.ok(updatedBoardList);
    }



}
