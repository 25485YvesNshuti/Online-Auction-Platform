package OnlineAuctionPlatform.controller;

import OnlineAuctionPlatform.model.SellerProfile;
import OnlineAuctionPlatform.service.AdminSellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/sellers")
public class AdminSellerController {
        @GetMapping("")
        public String redirectToDashboard() {
            return "redirect:/admin/dashboard";
        }
    @Autowired
    private AdminSellerService adminSellerService;


    @GetMapping("/{id}")
    public String getSellerDetails(@PathVariable("id") UUID sellerId, Model model) {
        Optional<SellerProfile> profileOpt = adminSellerService.getSellerDetails(sellerId);
        if (profileOpt.isPresent()) {
            SellerProfile seller = profileOpt.get();
            model.addAttribute("seller", seller);
            model.addAttribute("nationalIdDocUrl", getFileUrl(seller.getNationalIdOrPassport()));
            model.addAttribute("proofOfAccountOwnershipUrl", getFileUrl(seller.getProofOfAccountOwnershipUrl()));
            model.addAttribute("authorizationLetterUrl", getFileUrl(seller.getAuthorizationLetterUrl()));
            model.addAttribute("noDisputeDeclarationUrl", getFileUrl(seller.getNoDisputeDeclarationUrl()));
            model.addAttribute("courtSaleAuthorizationUrl", getFileUrl(seller.getCourtSaleAuthorizationUrl()));
            model.addAttribute("ownershipCertificateUrl", getFileUrl(seller.getOwnershipCertificateUrl()));
            model.addAttribute("valuationReportUrl", getFileUrl(seller.getValuationReportUrl()));
            model.addAttribute("publicationNoticeUrl", getFileUrl(seller.getPublicationNoticeUrl()));
            model.addAttribute("executionCaseDocumentsUrl", getFileUrl(seller.getExecutionCaseDocumentsUrl()));
            return "admin/seller-details";
        } else {
            model.addAttribute("error", "Seller not found");
            return "admin/seller-details";
        }
    }

    private String getFileUrl(String filePath) {
        if (filePath == null || filePath.isEmpty()) return null;
        String clean = filePath.replaceAll("^/+|uploads/+", "");
        return "/uploads/" + clean;
    }

    @PostMapping("/{id}/approve")
    public String approveSeller(@PathVariable("id") UUID sellerId, Model model) {
        boolean success = adminSellerService.approveSeller(sellerId);
        model.addAttribute("success", success);
        return "redirect:/admin/sellers/" + sellerId;
    }

    @PostMapping("/{id}/reject")
    public String rejectSeller(@PathVariable("id") UUID sellerId, @RequestParam("reason") String reason, Model model) {
        boolean success = adminSellerService.rejectSeller(sellerId, reason);
        model.addAttribute("success", success);
        return "redirect:/admin/sellers/" + sellerId;
    }

    @PostMapping("/{id}/request-info")
    public String requestMoreInfo(@PathVariable("id") UUID sellerId, Model model) {
        boolean success = adminSellerService.requestMoreInfo(sellerId);
        model.addAttribute("success", success);
        return "redirect:/admin/sellers/" + sellerId;
    }
}
