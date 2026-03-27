package in.manikanta.moneymanager.service;

import in.manikanta.moneymanager.dto.ExpenseDTO;
import in.manikanta.moneymanager.dto.IncomeDTO;
import in.manikanta.moneymanager.entity.CategoryEntity;
import in.manikanta.moneymanager.entity.ExpenseEntity;
import in.manikanta.moneymanager.entity.IncomeEntity;
import in.manikanta.moneymanager.entity.ProfileEntity;
import in.manikanta.moneymanager.repository.CategoryRepository;
import in.manikanta.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IncomeService {

    private  final CategoryService categoryService;

    private final IncomeRepository incomeRepository;

    private final ProfileService profileService;

    private final CategoryRepository categoryRepository;


    // add a new Income to database
    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not Found"));
        IncomeEntity newIncome = toEnity(dto,profile,category);
        newIncome=incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    //Retrive all incomes for current month/based on the start date and end date

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentuser(){
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now= LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    //Get latest 5 icomes of current user

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity> list =    incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Get totoal income of current user
    public BigDecimal getTotalIncomeForCurrentUset(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
        return total != null ? total: BigDecimal.ZERO;
    }

    //delete incomes by current user id

    public void deleteIncome(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();

        IncomeEntity entity = incomeRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized access to income");
        }
        incomeRepository.delete(entity);
    }

    //filter incomes
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);

        return list.stream().map(this :: toDTO).toList();
    }

    private IncomeEntity toEnity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();


    }

    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName(): "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }
}
